package ca.ualberta.cs.queueunderflow.test;



import java.util.ArrayList;

import junit.framework.TestCase;
import ca.ualberta.cs.queueunderflow.Answer;
import ca.ualberta.cs.queueunderflow.AnswerList;
import ca.ualberta.cs.queueunderflow.QuestionList;
import ca.ualberta.cs.queueunderflow.Question;
import ca.ualberta.cs.queueunderflow.Reply;

public class QuestionListTest extends TestCase {
	
	//Use case 4
	public void testAddQuestion() {
		QuestionList questionList= new QuestionList();
		String questionName= "How does this work?";
		AnswerList answerList=new AnswerList ();
		ArrayList <Reply> question_replies= new ArrayList <Reply >();
		String author= "Me";
		Question questionTest= new Question(questionName,answerList, question_replies,author);
		questionList.add(questionTest);
		
		assertTrue("Question List isn't empty", questionList.size()==1);
	}
	
	
	//Use case 5
	public void testAnswerQuestion() {
		QuestionList questionList= new QuestionList();
		String questionName= "A question?";
		AnswerList answerList= new AnswerList();
		ArrayList <Reply> question_replies= new ArrayList <Reply >();
		String author= "Me";
		Question questionTest= new Question(questionName,answerList, question_replies,author);
		questionList.add(questionTest);
		
		//Testing the questionIndex method of questionlist class
		int question_index= questionList.questionIndex(questionTest);
		assertTrue("question index is 0", question_index==0);
		
		//Adding an answer
		Question sameQuestion= questionList.get(question_index);
		ArrayList<Reply> answer_replies= new ArrayList<Reply>();
		String answerName= "An answer";
		String authorName= "You";
		Answer testAnswer= new Answer(answerName, answer_replies,authorName);
		
		sameQuestion.addAnswer(testAnswer);
		
		//Testing to see if the answer_list of a question is not empty
		questionList.set(question_index, sameQuestion);
		assertTrue("Answer List isn't empty",questionList.get(question_index).getAnswerListSize()==1 ) ;
		
	}
	
	//use case 14
	public void testAnswersQuestionRec() {
		QuestionList questionList= new QuestionList();
		String questionName= "A question?";
		AnswerList answerList= new AnswerList();
		ArrayList <Reply> question_replies= new ArrayList <Reply >();
		String author= "A author";
		//Add question_replies
		Question questionTest= new Question(questionName, answerList,question_replies,author);
		questionList.add(questionTest);
		
		//Testing the questionIndex method of questionlist class
		int question_index= questionList.questionIndex(questionTest);
		assertTrue("question index is 0", question_index==0);
		
		//Adding an answer
		Question sameQuestion= questionList.get(question_index);
		ArrayList<Reply> answer_replies= new ArrayList<Reply>();
		String author2="Author 2";
		String answerName= "An answer";
		String answerName2= "Answer 2";
		String answerName3= "Answer 3";
		
		Answer testAnswer= new Answer(answerName, answer_replies,author2);
		Answer testAnswer2= new Answer(answerName2,answer_replies,author2);
		Answer testAnswer3= new Answer(answerName3, answer_replies,author2);
		
		sameQuestion.addAnswer(testAnswer);
		sameQuestion.addAnswer(testAnswer2);
		sameQuestion.addAnswer(testAnswer3);
		
		
		questionList.set(question_index, sameQuestion);
		
		//Testing to see how many answers a question has received
		int question_index2= questionList.questionIndex(sameQuestion);
		Question sameQuestion2= questionList.get(question_index2);
		assertTrue("The answers this question received=3", sameQuestion2.getAnswerListSize()==3);
	}
	
	//use case 3
	public void testRepliesReceived() {
		QuestionList questionList= new QuestionList();
		String questionName= "A question?";
		AnswerList answerList= new AnswerList();
		ArrayList <Reply> question_replies= new ArrayList <Reply >();
		String author="A author";
		//Adding replies to question
		String reply_author= "I dunno";
		Reply q_reply= new Reply("Whats going on",reply_author);
		//question_replies.add(q_reply);
		
		Question questionTest= new Question(questionName, answerList,question_replies,author);
		questionTest.addQuestionReply(q_reply);
		questionList.add(questionTest);
		
		//Testing the questionIndex method of questionlist class
		int question_index= questionList.questionIndex(questionTest);
		assertTrue("question index is 0", question_index==0);
		
		//Adding an answer
		Question sameQuestion= questionList.get(question_index);
		ArrayList<Reply> answer_replies= new ArrayList<Reply>();
		String answerName= "An answer";
		
		//Add replies to answer
		//answer_replies.add("Answer reply 1");
		
		String answer_author= "Me";
		String answer_r_author= "7-11";
		Answer testAnswer= new Answer(answerName, answer_replies, answer_author);
		Reply a_reply= new Reply("Go to stackoverflow",answer_r_author);
		testAnswer.addReply(a_reply);
		
		sameQuestion.addAnswer(testAnswer);
		
		
		questionList.set(question_index, sameQuestion);
		
		//Testing to see how many answers a question has received
		int question_index2= questionList.questionIndex(sameQuestion);
		Question sameQuestion2= questionList.get(question_index2);
		assertTrue("The answers this question received=1", sameQuestion2.getAnswerListSize()==1);
		
		//See replies to question
		assertTrue("Question replies = 1", sameQuestion2.getSizeReplies()==1);
		
		AnswerList answers= sameQuestion2.getAnswerList();
		Answer testAnswer2= answers.getAnswer(0);
		assertTrue("Answer replies=1", testAnswer2.getSizeReplies()==1 );
	}
	

}