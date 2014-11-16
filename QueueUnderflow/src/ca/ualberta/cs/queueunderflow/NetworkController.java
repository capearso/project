package ca.ualberta.cs.queueunderflow;

import java.util.UUID;

import ca.ualberta.cs.queueunderflow.models.Answer;
import ca.ualberta.cs.queueunderflow.models.Question;
import ca.ualberta.cs.queueunderflow.models.QuestionList;
import ca.ualberta.cs.queueunderflow.models.Reply;

// Implement an interface?
// NOTE : This assumes that the MasterList in ListHandler is always completely online, thus we add to that list
// to ensure that there are no conflicts
public class NetworkController {

	private ESManager esManager;
	
	public NetworkController() {
		esManager = new ESManager();
	}
	
	public void addQuestion(Question newQuestion) {
		Thread thread = new AddQThread(newQuestion);
		thread.start();
		
		ListHandler.getMasterQList().add(newQuestion);
		ListHandler.getMyQsList().add(newQuestion);
	}
	
	public void addAnswer(UUID questionID, Answer newAnswer) {
		Thread thread = new AddAThread(questionID, newAnswer);
		thread.start();
		
		// So it updates the view b/c we use .set
		int questionIndex = ListHandler.getMasterQList().getIndexFromID(questionID);
		Question question = ListHandler.getMasterQList().get(questionIndex);
//		question.addAnswer(newAnswer);
		ListHandler.getMasterQList().set(questionIndex, question);
	}
	
	// Everything below here - still need to push to network
	public void addQReply(UUID questionID, Reply reply) {
		int questionIndex = ListHandler.getMasterQList().getIndexFromID(questionID);
		Question question = ListHandler.getMasterQList().get(questionIndex);
		question.addReply(reply);
		ListHandler.getMasterQList().set(questionIndex, question);
	}
	
	public void addAReply(UUID questionID, UUID answerID, Reply reply) {
		int questionIndex = ListHandler.getMasterQList().getIndexFromID(questionID);
		Question question = ListHandler.getMasterQList().get(questionIndex);
		
		int answerIndex = question.getAnswerList().getIndexFromID(answerID);
		Answer answer = question.getAnswerList().getAnswer(answerIndex);
		
		answer.addReply(reply);
		ListHandler.getMasterQList().set(questionIndex, question);
	}
	
	
    
    
    
    // BELOW - Maybe move this somewhere else later
	// This is modified from https://github.com/dfserrano/AndroidElasticSearch 11-15-2014
	class AddQThread extends Thread {

		private Question question;
		
		public AddQThread(Question question) {
			this.question = question;
		}
		
		@Override
		public void run() {
			esManager.addQuestion(question);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	class AddAThread extends Thread {

		private UUID questionID;
		private Answer answer;
		
		public AddAThread(UUID questionID, Answer answer) {
			this.questionID = questionID;
			this.answer = answer;
		}
		
		@Override
		public void run() {
			esManager.addAnswer(questionID, answer);
			
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
