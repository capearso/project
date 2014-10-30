package ca.ualberta.cs.queueunderflow.controllers;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import ca.ualberta.cs.queueunderflow.ListHandler;
import ca.ualberta.cs.queueunderflow.R;
import ca.ualberta.cs.queueunderflow.User;
import ca.ualberta.cs.queueunderflow.models.Answer;
import ca.ualberta.cs.queueunderflow.models.Question;
import ca.ualberta.cs.queueunderflow.models.QuestionList;
import ca.ualberta.cs.queueunderflow.models.Reply;

public class WriteReplyController {

	public static final int HOME_SCREEN_FRAGMENT = 1;
	public static final int FAVORITES_FRAGMENT = 2;
	public static final int READING_LIST_FRAGMENT = 3;
	public static final int MY_QUESTIONS_FRAGMENT = 4;
	
	private static int TYPE_QUESTION = 0;
	private static int TYPE_ANSWER = 1;
	
	private Activity activity;
	private View view;
	
	public WriteReplyController(Activity activity, View view) {
		this.activity = activity;
		this.view = view;
	}
	
	public void addReply(Bundle arguments) {
		
		// Retrieves info
		int fromFragment = arguments.getInt("fromFragment");
		int questionPosition = arguments.getInt("questionPosition");
		int type = arguments.getInt("type");									// whether we're adding a reply to a question or an answer
		
		QuestionList questionList = findQuestionList(fromFragment);
		Question question = questionList.get(questionPosition);
		
		// Creating the reply
		EditText replyText = (EditText) view.findViewById(R.id.replyText);
		Reply newReply;
		try {
			newReply = new Reply(replyText.getText().toString(), User.getUserName());
		} catch (IllegalArgumentException e) {
			Toast.makeText(activity.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
			return;
		}
		
		// Adding the reply to the proper Question or Answer
		if (type == TYPE_QUESTION) {
			question.addQuestionReply(newReply);
			questionList.set(questionPosition, question);
		}
		else if (type == TYPE_ANSWER) {
			int answerPosition = arguments.getInt("answerPosition");
			Answer answer = question.getAnswerList().getAnswer(answerPosition);
			answer.addReply(newReply);
			questionList.set(questionPosition, question);
		}
	}

	
	// Retrieves the question from the fragment where it was selected from
    private QuestionList findQuestionList(int fromFragment) {
		switch (fromFragment) {
		case (HOME_SCREEN_FRAGMENT):
			return ListHandler.getMasterQList();
		case (FAVORITES_FRAGMENT):
			return ListHandler.getFavsList();
		case (READING_LIST_FRAGMENT):
			return ListHandler.getReadingList();
		case (MY_QUESTIONS_FRAGMENT):
			return ListHandler.getMyQsList();
		}
		
		// Just to satisfy Java
		return ListHandler.getMasterQList();
    }
	
}
