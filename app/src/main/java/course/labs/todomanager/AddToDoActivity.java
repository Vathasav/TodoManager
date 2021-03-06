package course.labs.todomanager;

import java.util.Calendar;
import java.util.Date;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import course.labs.todomanager.ToDoItem.Priority;
import course.labs.todomanager.ToDoItem.Status;
import android.widget.Toast;


/***
 * View for creating a new todo activity
 * creates layout for new todo
 * fills apprpriate information
 */

public class AddToDoActivity extends Activity {

	// 7 days in milliseconds - 7 * 24 * 60 * 60 * 1000
	private static final int SEVEN_DAYS = 604800000;

	private static final String TAG = "Lab-UserInterface";

	private static String timeString;
	private static String dateString;
	private static TextView dateView;
	private static TextView timeView;

	private Date mDate;
	private RadioGroup mPriorityRadioGroup;
	private RadioGroup mStatusRadioGroup;
	private EditText mTitleText;
	private RadioButton mDefaultStatusButton;
	private RadioButton mDefaultPriorityButton;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

                Intent intent = getIntent();


		setContentView(R.layout.add_todo);


		mTitleText = (EditText) findViewById(R.id.title);
		mDefaultStatusButton = (RadioButton) findViewById(R.id.statusNotDone);
		mDefaultPriorityButton = (RadioButton) findViewById(R.id.medPriority);
		mPriorityRadioGroup = (RadioGroup) findViewById(R.id.priorityGroup);
		mStatusRadioGroup = (RadioGroup) findViewById(R.id.statusGroup);
		dateView = (TextView) findViewById(R.id.date);
		timeView = (TextView) findViewById(R.id.time);

		// Set the default date and time

		setDefaultDateTime();

	        ActionBar actionBar = getActionBar();
	        View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
	        actionBar.setCustomView(mActionBarView);
	        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

	        if(intent.getExtras() != null ) {
	
	            if (intent.getStringExtra(ToDoItem.TITLE) != null)
	                mTitleText.setText(intent.getStringExtra(ToDoItem.TITLE));
	
	
	            if (intent.getExtras().get(ToDoItem.PRIORITY) != null) {
	                Priority priority = (Priority) intent.getExtras().get(ToDoItem.PRIORITY);
	
	                setPriority(priority);
	
	
	            }
	
	
	            if (intent.getExtras().get(ToDoItem.STATUS) != null) {
	                Status status = (Status) intent.getExtras().get(ToDoItem.STATUS);
	
	                setStatus(status);
	              //  mStatusRadioGroup.check(status.ordinal());
	
	            }
	
	            if (intent.getExtras().get(ToDoItem.DATE) != null) {
	
	                Date date = (Date) intent.getExtras().get(ToDoItem.DATE);
	
	                mDate = date;
	
	                Calendar c = Calendar.getInstance();
	                c.setTime(mDate);
	
	                setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
	                        c.get(Calendar.DAY_OF_MONTH));
	
	                dateView.setText(dateString);
	
	                setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
	                        c.get(Calendar.MILLISECOND));
	
	                timeView.setText(timeString);
	
	            }
	        }


        //start action mode
        //mActionMode = startActionMode(mActionModeCallback);

		// OnClickListener for the Date button, calls showDatePickerDialog() to
		// show
		// the Date dialog



		//final Button datePickerButton = (Button) findViewById(R.id.date_picker_button);
        	dateView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showDatePickerDialog();
			}
		});

		// OnClickListener for the Time button, calls showTimePickerDialog() to
		// show the Time Dialog

		//final Button timePickerButton = (Button) findViewById(R.id.time_picker_button);
		timeView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showTimePickerDialog();
			}
		});

		// OnClickListener for the Cancel Button,

		final TextView cancelButton = (TextView) findViewById(R.id.cancel);

		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Log.i(TAG, "Entered cancelButton.OnClickListener.onClick()");


//                Toast.makeText(getApplicationContext(),
//                        "cancelled", Toast.LENGTH_SHORT).show();

                setResult(RESULT_CANCELED);
                finish();

			}
		});

		// TODO - Set up OnClickListener for the Reset Button
	//	final TextView resetButton = (TextView) findViewById(R.id.todo);
	/*	resetButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Entered resetButton.OnClickListener.onClick()");

				// TODO - Reset data to default values


                mTitleText.getText().clear();

                mPriorityRadioGroup.check(R.id.medPriority);
                mStatusRadioGroup.check(R.id.statusNotDone);

                setDefaultDateTime();

			}
		});
*/
		// Set up OnClickListener for the Submit Button

		final TextView submitButton = (TextView) findViewById(R.id.save);
		submitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(TAG, "Entered submitButton.OnClickListener.onClick()");

				// gather ToDoItem data

				// TODO - Get the current Priority
				Priority priority = getPriority();

				// TODO - Get the current Status
				Status status = getStatus();

				// TODO - Get the current ToDoItem Title
				String titleString = getToDoTitle();

				// Construct the Date string
				String fullDate = dateString + " " + timeString;

				// Package ToDoItem data into an Intent
				Intent data = new Intent();
				ToDoItem.packageIntent(data, titleString, priority, status,
						fullDate);

				// TODO - return data Intent and finish

                setResult(RESULT_OK,data);

                finish();

			}
		});
	}

    private void setStatus(Status status) {

        int id ;


        switch (status) {
            case DONE:
                id = R.id.statusDone;
                break;
            case NOTDONE:
                id = R.id.statusNotDone;
                break;
            default: {
                id = R.id.statusNotDone;
            }
        }

            mStatusRadioGroup.check(id);
    }

    private void setPriority(Priority priority) {

        int priorityId;

        switch (priority) {
            case LOW:
                priorityId = R.id.lowPriority;
            break;
            case MED:
                priorityId = R.id.medPriority;
                break;
            case HIGH:
                priorityId = R.id.highPriority;
                break;
            default: {
                priorityId = R.id.medPriority;

            }
        }

        mPriorityRadioGroup.check(priorityId);

    }

  /*  private ActionMode mActionMode = null;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items


            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_actions, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.deleteAll:
                    deleteCurrentItem(mode);

                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };
*/

    // Do not modify below this point.

	private void setDefaultDateTime() {

		// Default is current time + 7 days
		mDate = new Date();
		mDate = new Date(mDate.getTime() + SEVEN_DAYS);

		Calendar c = Calendar.getInstance();
		c.setTime(mDate);

		setDateString(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
				c.get(Calendar.DAY_OF_MONTH));

		dateView.setText(dateString);

		setTimeString(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
				c.get(Calendar.MILLISECOND));

		timeView.setText(timeString);
	}

	private static void setDateString(int year, int monthOfYear, int dayOfMonth) {

		// Increment monthOfYear for Calendar/Date -> Time Format setting
		monthOfYear++;
		String mon = "" + monthOfYear;
		String day = "" + dayOfMonth;

		if (monthOfYear < 10)
			mon = "0" + monthOfYear;
		if (dayOfMonth < 10)
			day = "0" + dayOfMonth;

		dateString = year + "-" + mon + "-" + day;
	}

	private static void setTimeString(int hourOfDay, int minute, int mili) {
		String hour = "" + hourOfDay;
		String min = "" + minute;

		if (hourOfDay < 10)
			hour = "0" + hourOfDay;
		if (minute < 10)
			min = "0" + minute;

		timeString = hour + ":" + min + ":00";
	}

	private Priority getPriority() {

		switch (mPriorityRadioGroup.getCheckedRadioButtonId()) {
		case R.id.lowPriority: {
			return Priority.LOW;
		}
		case R.id.highPriority: {
			return Priority.HIGH;
		}
		default: {
			return Priority.MED;
		}
		}
	}

	private Status getStatus() {

		switch (mStatusRadioGroup.getCheckedRadioButtonId()) {
		case R.id.statusDone: {
			return Status.DONE;
		}
		default: {
			return Status.NOTDONE;
		}
		}
	}

	private String getToDoTitle() {
		return mTitleText.getText().toString();
	}
	
	
	// DialogFragment used to pick a ToDoItem deadline date

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current date as the default date in the picker

			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			setDateString(year, monthOfYear, dayOfMonth);

			dateView.setText(dateString);
		}

	}

	// DialogFragment used to pick a ToDoItem deadline time

	public static class TimePickerFragment extends DialogFragment implements
			TimePickerDialog.OnTimeSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);

			// Create a new instance of TimePickerDialog and return
			return new TimePickerDialog(getActivity(), this, hour, minute, true);
		}

		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			setTimeString(hourOfDay, minute, 0);

			timeView.setText(timeString);
		}
	}

	private void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getFragmentManager(), "datePicker");
	}

	private void showTimePickerDialog() {
		DialogFragment newFragment = new TimePickerFragment();
		newFragment.show(getFragmentManager(), "timePicker");
	}
}
