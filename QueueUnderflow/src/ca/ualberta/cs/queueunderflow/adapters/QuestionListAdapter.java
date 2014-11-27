package ca.ualberta.cs.queueunderflow.adapters;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import ca.ualberta.cs.queueunderflow.Buffer;
import ca.ualberta.cs.queueunderflow.ListHandler;
import ca.ualberta.cs.queueunderflow.LoadSave;
import ca.ualberta.cs.queueunderflow.NetworkBuffer;
import ca.ualberta.cs.queueunderflow.NetworkController;
import ca.ualberta.cs.queueunderflow.NetworkManager;
import ca.ualberta.cs.queueunderflow.R;
import ca.ualberta.cs.queueunderflow.User;
import ca.ualberta.cs.queueunderflow.models.Question;
import ca.ualberta.cs.queueunderflow.views.AddAnAnswerActivity;
import ca.ualberta.cs.queueunderflow.views.MainActivity;
import ca.ualberta.cs.queueunderflow.views.QAViewActivity;
import ca.ualberta.cs.queueunderflow.views.WriteReplyDialogFragment;

/**
 * The Class QuestionListAdapter.
 * Connects a list of questions to the SuperFragment
 * Handles favoriting & adding to the reading list
 * Handles switching to the AddAAnswerActivity & calling the WriteReplyDialogFragment to be displayed
 *@author group 10
 * @version 0.5
 */
public class QuestionListAdapter extends ArrayAdapter<Question> {
	
	/** The type question. */
	private static int TYPE_QUESTION = 0;
	
	/** The inflater. */
	private LayoutInflater inflater;
	
	/** The layout id. */
	private int layoutID;
	
	/** The question array. */
	private ArrayList<Question> questionArray;
	
	/** The activity. */
	private Activity activity;
	
	/** The from fragment. */
	private int fromFragment;
	
	/**
	 * Instantiates a new question list adapter.
	 *
	 * @param activity the activity
	 * @param layoutID the layout id
	 * @param questionArray the question array
	 * @param fromFragment the from fragment
	 */
	public QuestionListAdapter(Activity activity, int layoutID, ArrayList<Question> questionArray, int fromFragment) {
		super(activity, layoutID, questionArray);
		
		// this line is taken from http://www.survivingwithandroid.com/2012/10/android-listview-custom-adapter-and.html
		this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		this.activity = activity;
		this.layoutID = layoutID;
		this.questionArray = questionArray;
		this.fromFragment = fromFragment;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final View view;
		if (questionArray.get(position).hasPicture()) {
			view = inflater.inflate(R.layout.list_item_question_picture, parent, false);
		}
		else {
			view = inflater.inflate(layoutID, parent, false);
		}
		
		//By default, display an empty location textview
		//TextView locationDisplay= (TextView) view.findViewById(R.id.locationTextView);
		//locationDisplay.setText("");
		
		TextView questionDisplay = (TextView) view.findViewById(R.id.questionTextView);
		questionDisplay.setText(questionArray.get(position).getName());
		
		TextView authorDisplay = (TextView) view.findViewById(R.id.authorTextView);
		authorDisplay.setText(questionArray.get(position).getAuthor());
		
		TextView dateDisplay = (TextView) view.findViewById(R.id.dateTextView);
		dateDisplay.setText(DateFormat.format("MMM dd, yyyy", questionArray.get(position).getDate()));
		
		TextView locationDisplay = (TextView) view.findViewById(R.id.locationTextView);
		locationDisplay.setText(questionArray.get(position).getLocation());
		
		TextView upvoteDisplay = (TextView) view.findViewById(R.id.upvoteDisplay);
		upvoteDisplay.setText(Integer.toString(questionArray.get(position).getUpvotes()));
		
		
		final ImageButton upvoteBtn = (ImageButton) view.findViewById(R.id.upvoteBtn);
		upvoteBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				User user= ListHandler.getUser();
				Question question= questionArray.get(position);
				if (user.alreadyUpvotedQuestion(question)) {
					Toast.makeText(getContext(), "Question was already upvoted", Toast.LENGTH_SHORT).show();
				}
				else {
					boolean isInFavorites = false;
                    if(ListHandler.getFavsList().getQuestionList().contains(questionArray.get(position))) isInFavorites = true;
				    
                    // FIX THIS TO ADD ONLY THE QUESTION ID INSTEAD OF THE ENTIRE QUESTION
					user.addUpvotedQuestion(questionArray.get(position));
					
					// To mimic fake view updates
					questionArray.get(position).upvoteResponse();
					TextView upvoteDisplay = (TextView) view.findViewById(R.id.upvoteDisplay);
					upvoteDisplay.setText(Integer.toString(questionArray.get(position).getUpvotes()));
					
					NetworkManager networkManager = NetworkManager.getInstance();
					if ( !networkManager.isOnline(activity.getApplicationContext()) ) {
						NetworkBuffer networkBuffer = networkManager.getNetworkBuffer();			
						networkBuffer.addQUpvote(question.getStringID());				
						return;
					}
					else { // if online
						NetworkController  networkController = new NetworkController();
						networkController.upvoteQuestion(questionArray.get(position).getStringID());
					}
					
				}

			}	
		});	
		
		if (User.getUseLocation()) {
			//String city= User.getCity();
			//String country=User.getCountry();
			//String location= city+","+country;
			String location= questionArray.get(position).getLocation();
			locationDisplay.setText(location);
		}
		
		if (questionArray.get(position).hasPicture() == true) {
			ImageButton hasPictureIcon = (ImageButton) view.findViewById(R.id.hasPictureIcon);
			hasPictureIcon.setVisibility(View.VISIBLE);
			
			ImageView imagePreview = (ImageView) view.findViewById(R.id.imagePreview);
			// TODO set imagePreview to the photo
			//String imagePath= questionArray.get(position).getImagePath();
			//imagePreview.setImageURI(Uri.parse(imagePath));
			String encodedImage= questionArray.get(position).getEncodedImage();
			byte [] imageBytes= Base64.decode(encodedImage.getBytes(), Base64.DEFAULT);
			imagePreview.setImageBitmap(BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length));
		}
		
		ImageButton answerBtn = (ImageButton) view.findViewById(R.id.answerBtn);
		answerBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(), AddAnAnswerActivity.class);
				//Pass the position of the question to the new activity
				intent.putExtra("question_position", position);
				intent.putExtra("fromFragment", fromFragment);
				intent.putExtra("questionID", questionArray.get(position).getStringID());
				activity.startActivity(intent);
				//update(null);
			}
		});
		
        ImageButton replyBtn = (ImageButton) view.findViewById(R.id.replyBtn);
        replyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				// Passing info about which questionList it's from, questionPosition & questionAnswer & the type (we're replying to an Answer) so we know where to add the reply to
				Bundle args = new Bundle();
				args.putInt("fromFragment", fromFragment);
				args.putInt("questionPosition", position);
				args.putString("questionID", questionArray.get(position).getStringID());
				args.putInt("type", TYPE_QUESTION);
				
				// Create & display reply dialog + attach arguments
				DialogFragment replyDialog = new WriteReplyDialogFragment();
				replyDialog.setArguments(args);
				replyDialog.show(activity.getFragmentManager(), null);
				
			}
		});
		
		CheckBox favBtn = (CheckBox) view.findViewById(R.id.favBtn);
		favBtn.setChecked(questionArray.get(position).getIsFav());
		favBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Question question = questionArray.get(position);
				question.setIsFav(isChecked);

				if (fromFragment == MainActivity.FAVORITES_FRAGMENT) {
					Buffer buffer = Buffer.getInstance();
					if (isChecked == false) {
						System.out.println("unfavoriting from fav buffer");
						buffer.addToFavBuffer(question.getStringID());
						
						//
						System.out.println("FAVLISTBUFFER CONTENT");
						System.out.println(buffer.favBuffer);
						//
					}
					else {
						System.out.println("favoriting from fav buffer");
						buffer.removeFromFavBuffer(question.getStringID());
						
						//
						System.out.println("FAVLISTBUFFER CONTENT");
						System.out.println(buffer.favBuffer);
						//
					}
				}
				
				else {
					if (isChecked == true) {
						System.out.println("favoriting normal");
						ListHandler.getFavsList().add(question);
						
						//
						System.out.println("FAVLIST CONTENT");
						ArrayList<String> favIDs = new ArrayList<String>();
						for (Question q : ListHandler.getFavsList().getQuestionList()) {
							favIDs.add(q.getStringID());
						}
						System.out.println(favIDs);
						//
					}
					else if (isChecked == false) {
						System.out.println("unfavoriting normal");
						ListHandler.getFavsList().remove(question);
						
						//
						System.out.println("FAVLIST CONTENT");
						ArrayList<String> favIDs = new ArrayList<String>();
						for (Question q : ListHandler.getFavsList().getQuestionList()) {
							favIDs.add(q.getStringID());
						}
						System.out.println(favIDs);
						//
					}
				}
				//Mark as unsaved changes,
				LoadSave.unsavedChanges = true;
			}
		});
		
		
		CheckBox readingListBtn = (CheckBox) view.findViewById(R.id.offlineBtn);
		readingListBtn.setChecked(questionArray.get(position).getIsInReadingList());
		readingListBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Question question = questionArray.get(position);
				question.setIsInReadingList(isChecked);
				
				if (fromFragment == MainActivity.READING_LIST_FRAGMENT) {
					Buffer buffer = Buffer.getInstance();
					if (isChecked == false) {
						buffer.addToReadingListBuffer(question.getStringID());
					}
					else {
						buffer.removeFromReadingListBuffer(question.getStringID());
					}
				}
				
				else {
					if (isChecked == true) {
						ListHandler.getReadingList().add(question);
					}
					else if (isChecked == false) {
						ListHandler.getReadingList().remove(question);
					}
				}
				
				//Mark as unsaved changes.
				LoadSave.unsavedChanges = true;
			}
		});
		return view;
	}
}
