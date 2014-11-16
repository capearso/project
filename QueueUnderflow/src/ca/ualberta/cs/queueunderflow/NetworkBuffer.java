package ca.ualberta.cs.queueunderflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import android.util.Log;
import ca.ualberta.cs.queueunderflow.models.Answer;
import ca.ualberta.cs.queueunderflow.models.Question;
import ca.ualberta.cs.queueunderflow.models.Reply;

// implement generic buffer class?
// This deals with all the actions that users do when they are offline
public class NetworkBuffer {

	// Add & handle upvotes, favorites, reading list indicator here later
	ArrayList<Question> questionBuffer;
	Map<UUID, Answer> answerBuffer;
	Map<UUID, Reply> questionReplyBuffer;
	Map<UUID, UUID> qaIDBuffer;
	Map<UUID, Reply> answerReplyBuffer;
	
	public NetworkBuffer() {
		questionBuffer = new ArrayList<Question>();
		answerBuffer = new Hashtable<UUID, Answer>();
		questionReplyBuffer = new Hashtable<UUID, Reply>();
		qaIDBuffer = new Hashtable<UUID, UUID>();
		answerReplyBuffer = new HashMap<UUID, Reply>();
	}
	
	public void addQuestion(Question question) {
		questionBuffer.add(question);
		Log.d("network", "adding question to buffer: " + question.getName());
	}
	
	// Problem : What is you add more than one answer while offline - dictionaries don't work anymore - Need to fix later
	public void addAnswer(UUID questionID, Answer answer) {
		answerBuffer.put(questionID, answer);
		Log.d("network", "adding answer to buffer: " + answer.getName());
	}
	
	public void addQReply(UUID questionID, Reply reply) {
		questionReplyBuffer.put(questionID, reply);
	}
	
	public void addAReply(UUID questionID, UUID answerID, Reply reply) {
		qaIDBuffer.put(questionID, answerID);
		answerReplyBuffer.put(answerID, reply);
	}
	
	private void clearAll() {
		questionBuffer.clear();
		answerBuffer.clear();
		questionReplyBuffer.clear();
		qaIDBuffer.clear();
		answerReplyBuffer.clear();
	}
	
	public void flushAll() {
		
		NetworkController networkController = new NetworkController();
		
		// Push add questions in buffer online
		for (Question question : questionBuffer) {
			networkController.addQuestion(question);
			Log.d("flush buffer", question.getName());
		}
		
		// Push all answers to question in buffer online
		Set<UUID> answerBufferKeys = answerBuffer.keySet();
		for(UUID id : answerBufferKeys) {
			Answer answer = answerBuffer.get(id);
			networkController.addAnswer(id, answer);
			Log.d("flush buffer", answer.getName());
		}
		
		// Push all replies to a question in buffer online
		Set<UUID> questionReplyBufferKeys = questionReplyBuffer.keySet();
		for(UUID id : questionReplyBufferKeys) {
			Reply reply = questionReplyBuffer.get(id);
			networkController.addQReply(id, reply);
		}
		
		// Push all replies to an answer in buffer online
		Set<UUID> qaIDBufferKeys = qaIDBuffer.keySet();
		for(UUID questionID : qaIDBufferKeys) {
			UUID answerID = qaIDBuffer.get(questionID);
			Reply reply = answerReplyBuffer.get(answerID);
			networkController.addAReply(questionID, answerID, reply);
		}
		
		clearAll();
	
	}
}
