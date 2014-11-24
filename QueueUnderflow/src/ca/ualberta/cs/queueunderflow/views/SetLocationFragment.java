package ca.ualberta.cs.queueunderflow.views;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import ca.ualberta.cs.queueunderflow.LoadSave;
import ca.ualberta.cs.queueunderflow.LocationHandler;
import ca.ualberta.cs.queueunderflow.NetworkManager;
import ca.ualberta.cs.queueunderflow.R;
import ca.ualberta.cs.queueunderflow.User;

public class SetLocationFragment extends Fragment implements OnClickListener{

	LocationHandler lHandler;
	private GetGPSBackground gpsThread;
	
	@Override
	public void onStop() {
		super.onStop();
		//Stop thread from continuing before we leave the activity
		if(gpsThread != null) gpsThread.cancel(true);
		//Stop GPS from listening before we leave the activity.
		if(lHandler != null) lHandler.GPSUnlisten();
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Home");
        return inflater.inflate(R.layout.activity_set_location_fragment, container, false);
    }

    public void onClick(View view)
    {
    	//Occurs when the get GPS button is clicked.
    	
    	final Activity act = getActivity();
    	final EditText latitude = (EditText) getView().findViewById(R.id.txtCity);
    	final EditText longitude = (EditText) getView().findViewById(R.id.txtCountry);
    	final TextView status = (TextView) getView().findViewById(R.id.textViewStatus);
    	// Move this into a controller once this works...

    	boolean error = false;
    	
    	//Create LocationHandler...
    	lHandler = new LocationHandler(getActivity());
    	
    	//See if GPS is enabled.
    	if(!lHandler.isGPSEnabled())
    	{
    		error = true;
    		Toast.makeText(getActivity(), "GPS is disabled. Enable it and try again.", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    	//See if there is network connectivity.
    	NetworkManager network = NetworkManager.getInstance();
    	if(!network.isOnline(getActivity()) && !error)
    	{
    		error = true;
    		Toast.makeText(getActivity(), "Internet access is required to get your location.", Toast.LENGTH_SHORT).show();
    	}
    	
    	
    	//Try to get GPS lock if there are no errors
    	if(!error)
    	{
    		
    		lHandler.GetGPSLocation();
    		gpsThread = new GetGPSBackground();
    		gpsThread.execute();
    		status.setText("Waiting for response from GPS...");
    	}
    	
    }
    
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    	// Idea of using the class as OnClickListener from:
    	// http://stackoverflow.com/questions/6091194/how-to-handle-button-clicks-using-the-xml-onclick-within-fragments
    	// by Adorjan Princz
    	Button b = (Button) getActivity().findViewById(R.id.buttonGetLocation);
    	b.setOnClickListener(this); //Set get from GPS button listener to OnClick() in this class.

    	final CheckBox useLocation = (CheckBox) getActivity().findViewById(R.id.use_location_data);
    	final EditText city = (EditText) getActivity().findViewById(R.id.txtCity);
    	final EditText country = (EditText) getActivity().findViewById(R.id.txtCountry);
    	
    	//Set fields to data in user...
    	useLocation.setChecked(User.getUseLocation());
    	city.setText(User.getCity());
    	country.setText(User.getCountry());
    	
    	Button submit = (Button) getActivity().findViewById(R.id.buttonSubmit);
    	submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				String sCity = city.getText().toString();
				String sCountry = country.getText().toString();
				boolean isChecked = useLocation.isChecked();
				
				User.setUseLocation(isChecked);
				User.setCity(sCity);
				User.setCountry(sCountry);
			
				LoadSave ls = LoadSave.getInstance();
				ls.saveCity(sCity);
				ls.saveCountry(sCountry);
				ls.saveUseLocation(isChecked);
				
				Toast.makeText(getActivity(), "Location data successfully saved!", Toast.LENGTH_SHORT).show();
				
			}
    		
    	});
    	
    }
    
    //GPS thread.
    public class GetGPSBackground extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			while(LocationHandler.listeningGPS){
				//Waiting for response...
			}
			
			//publishProgress("GPS Location Found!");
			
			//Now get city name...
			publishProgress("Getting locale information...");
			LocationHandler ls = new LocationHandler(getActivity());
			String locationString = ls.getLocationFromCoordinates(LocationHandler.latitude, LocationHandler.longitude);
			publishProgress("Location data found!");
			return locationString;
		}
    	
		protected void onPostExecute(String result) {
			
			String city = null;
			String country = null;
			
			if(result == null)
			{
				//No idea where the user is. Set everything to unknown. Show coordinates maybe?
				city = "Unknown";
				country = "Unknown";
			}else{
				Log.d("test", result);
				String[] results = result.split("\\|"); //Split the array by |. Since it's a regular expression, need to escape it.
				Log.d("test", results[0]);
				Log.d("test", results[1]);
				if(results.length == 2)
				{
					city = results[0];
					country = results[1];
				}
			}
			
			EditText cityText = (EditText) getActivity().findViewById(R.id.txtCity);
			EditText countryText = (EditText) getActivity().findViewById(R.id.txtCountry);
			
			cityText.setText(city);
			countryText.setText(country);
			
		}
		
		protected void onProgressUpdate(String... result) {
			TextView status = (TextView) getActivity().findViewById(R.id.textViewStatus);
			status.setText(result[0]);
		}
		
    }

}