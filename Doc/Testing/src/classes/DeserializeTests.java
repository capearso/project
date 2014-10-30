package classes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.sound.midi.SysexMessage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DeserializeTests {
	public static void main(final String[] args) throws IOException {
		
		/* Reply deserialization testing
		
		
	    // Configure GSON
		GsonBuilder gsonBuilder3= new GsonBuilder();
		Gson gson3= gsonBuilder3.create();
		final Reply reply3 = new Reply("This is a reply","Anonymous");
		String json3= gson3.toJson(reply3);
		
		System.out.println(json3);
	    final GsonBuilder gsonBuilder = new GsonBuilder();
	    gsonBuilder.registerTypeAdapter(Reply.class, new ReplySerializing());
	    
	    //This makes printing nicer
	    //gsonBuilder.setPrettyPrinting();
	    final Gson gson = gsonBuilder.create();

	    final Reply reply = new Reply("This is a reply","Anonymous");
	    

	    // Format to JSON
	    final String json = gson.toJson(reply);
	    System.out.println(json);
	    
	    gsonBuilder.registerTypeAdapter(Reply.class, new ReplyDeserializing());
	    Reply deserialized= gson.fromJson(json,Reply.class);
	   // System.out.println(deserialized.equals(reply));
	    System.out.println(reply.getReply()+" "+reply.getAuthor());
	    System.out.println(deserialized.getReply()+" "+deserialized.getAuthor());
		
		*/
		
		/*Date deserialization test
		
		Date date =new Date();
		
	    GsonBuilder gsonBuilder = new GsonBuilder();
	    
	    //May need a date serializer b/c date might end up being parsed wrong
	    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
	    Gson gson = gsonBuilder.create();
	    final String serial_date= gson.toJson(date);
	    System.out.println(serial_date);
		
	    gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializing());
	    Date deserial= gson.fromJson(serial_date,Date.class);
	    DateFormat df = new SimpleDateFormat("M/d/yy hh:mm a");
		String deserial_date= df.format(deserial);
	    System.out.println(deserial_date);
		*/
		
		
		//Answer deserialization test
		
		String answerName= "An answer";
		String authorName= "You";
		Answer testAnswer= new Answer(answerName,authorName);
		Reply answer_reply= new Reply("Please clarify what you're asking", "Anonymous");
		testAnswer.addReply(answer_reply);
		Picture pic= new Picture(10);
		testAnswer.setPicture(pic);
	
		ArrayList<Reply> replies;
		replies=testAnswer.getReplies();
	    Gson gson = new Gson();
	    String json=gson.toJson(replies);
		System.out.println(json);
		
		//Serialize the answer first then deserialize it and see if its the same
		
		
		final GsonBuilder gsonBuilder2 = new GsonBuilder();
	    gsonBuilder2.registerTypeAdapter(Answer.class, new AnswerSerializing());
	    gsonBuilder2.setPrettyPrinting();  
	    final Gson gson2 = gsonBuilder2.create();
	    final String json2=gson2.toJson(testAnswer);
	    System.out.println(json2);
	    
	    gsonBuilder2.registerTypeAdapter(Answer.class, new AnswerDeserializing());
	    Answer deserialized= gson2.fromJson(json2,Answer.class);
	   // System.out.println(answerName);
	    System.out.println(deserialized.getAnswer());
		
	 
	  }
}
