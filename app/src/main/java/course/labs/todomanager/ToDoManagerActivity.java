package course.labs.todomanager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.Date;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import course.labs.todomanager.ToDoItem.Priority;
import course.labs.todomanager.ToDoItem.Status;

/***
 *  Main Activity for managing todo items
 *  saves existing items to local storage
 *  loads existing items on application startup
 */
public class ToDoManagerActivity extends ListActivity {

	private static final int ADD_TODO_ITEM_REQUEST = 0;
        private static final int EDIT_TODO_ITEM_REQUEST = 1;
	private static final String FILE_NAME = "TodoManagerActivityData.txt";
	private static final String TAG = "Lab-UserInterface";

	// IDs for menu items
	private static final int MENU_DELETE = Menu.FIRST;
	private static final int MENU_DUMP = Menu.FIRST + 1;

	ToDoListAdapter mAdapter;

	private ToDoItem mEditedTodoItem;
	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

        @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);



        // Create a new TodoListAdapter for this ListActivity's ListView
	mAdapter = new ToDoListAdapter(ToDoManagerActivity.this);


	// TODO - Attach the adapter to this ListActivity's ListView

        ListView listView = getListView();
        listView.setAdapter(mAdapter);
        listView.setDividerHeight(15);

     /*   listView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(ToDoManagerActivity.this, AddToDoActivity.class);

                startActivity(myIntent);
            }
        });*/


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent myIntent = new Intent(ToDoManagerActivity.this, AddToDoActivity.class);

                mEditedTodoItem = (ToDoItem) mAdapter.getItem(i);


                myIntent.putExtra(ToDoItem.TITLE, mEditedTodoItem.getTitle());
                myIntent.putExtra(ToDoItem.PRIORITY, mEditedTodoItem.getPriority());
                myIntent.putExtra(ToDoItem.STATUS, mEditedTodoItem.getStatus());
                myIntent.putExtra(ToDoItem.DATE, mEditedTodoItem.getDate());


                startActivityForResult(myIntent,EDIT_TODO_ITEM_REQUEST);

            }
        });



        Log.i(TAG,"finished onCreate()");

        //setListAdapter(new ArrayAdapter<ToDoItem>(this,R.layout.todo_item,mAdapter));
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.i(TAG,"Entered onActivityResult()");

		// TODO - Check result code and request code
		// if user submitted a new ToDoItem
		// Create a new ToDoItem from the data Intent
		// and then add it to the adapter



        if(resultCode == RESULT_OK && requestCode == ADD_TODO_ITEM_REQUEST){


            if(data != null) {
                ToDoItem  newTodoItem = new ToDoItem(data);
                mAdapter.add(newTodoItem);
                setAlarm(newTodoItem);
            }

        }

        if(resultCode == RESULT_OK && requestCode == EDIT_TODO_ITEM_REQUEST){


            //TODO sort the array after editing list item

            if(data != null) {



                mEditedTodoItem.setTitle(data.getStringExtra(ToDoItem.TITLE));
                mEditedTodoItem.setPriority(Priority.valueOf(data.getStringExtra(ToDoItem.PRIORITY)));
                mEditedTodoItem.setStatus( Status.valueOf(data.getStringExtra(ToDoItem.STATUS)));

                try {
                    mEditedTodoItem.setDate(ToDoItem.FORMAT.parse(data.getStringExtra(ToDoItem.DATE)));
                } catch (ParseException e) {
                    mEditedTodoItem.setDate(new Date());
                }


                mAdapter.notifyDataSetChanged();
                setAlarm(mEditedTodoItem);
            }

        }



    }
    
    // append alarm to a todo item
    private void setAlarm(ToDoItem newTodoItem) {

        Intent intent = new Intent(getApplicationContext(), TodoItemReminder.class);

        intent.putExtra("Notify",newTodoItem.getTitle());


        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        long timeToFire = newTodoItem.getDate().getTime() - System.currentTimeMillis();


       alarmMgr.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+timeToFire,alarmIntent);


    }

    // Do not modify below here

	@Override
	public void onResume() {
		super.onResume();

		// Load saved ToDoItems, if necessary

		if (mAdapter.getCount() == 0)
			loadItems();
	}

	@Override
	protected void onPause() {
		super.onPause();

		// Save ToDoItems

		saveItems();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_daily,menu);

		menu.add(Menu.NONE, MENU_DELETE, Menu.NONE, "Delete all");
		menu.add(Menu.NONE, MENU_DUMP, Menu.NONE, "Dump to log");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_DELETE:
			mAdapter.clear();
			return true;
		case MENU_DUMP:
			dump();
			return true;
        case R.id.addNewToDo:
            Intent addIntent = new Intent(ToDoManagerActivity.this, AddToDoActivity.class);

            startActivityForResult(addIntent,ADD_TODO_ITEM_REQUEST);
            return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void dump() {

		for (int i = 0; i < mAdapter.getCount(); i++) {
			String data = ((ToDoItem) mAdapter.getItem(i)).toLog();
			Log.i(TAG,	"Item " + i + ": " + data.replace(ToDoItem.ITEM_SEP, ","));
		}

	}

	// Load stored ToDoItems
	private void loadItems() {
		BufferedReader reader = null;
		try {
			FileInputStream fis = openFileInput(FILE_NAME);
			reader = new BufferedReader(new InputStreamReader(fis));

			String title = null;
			String priority = null;
			String status = null;
			Date date = null;

			while (null != (title = reader.readLine())) {
				priority = reader.readLine();
				status = reader.readLine();
				date = ToDoItem.FORMAT.parse(reader.readLine());
				mAdapter.add(new ToDoItem(title, Priority.valueOf(priority),
						Status.valueOf(status), date));
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			if (null != reader) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// Save ToDoItems to file
	private void saveItems() {
		PrintWriter writer = null;
		try {
			FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
			writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					fos)));

			for (int idx = 0; idx < mAdapter.getCount(); idx++) {

				writer.println(mAdapter.getItem(idx));

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != writer) {
				writer.close();
			}
		}
	}
}
