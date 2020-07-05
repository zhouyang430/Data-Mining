package program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadData {
	private ArrayList<String> data = new ArrayList<String>();
	
	
	public ArrayList<String> getcsv (){
		return data;
	}
	

	public ReadData() {
		BufferedReader textFile =null;
		File csv = new File("./csv/Groceries.csv");
		
		try {
			textFile = new BufferedReader(new FileReader(csv));
			String lineData = "";
		//	int index = 0;
			while ((lineData = textFile.readLine()) != null){
				data.add(lineData);
				//System.out.println(data.get(index));
		//		index++;
			}
			textFile.close();
			}
		catch (FileNotFoundException e){
			System.out.println("file not found");
			}
		catch (IOException e){
			System.out.println("IO exception");
			}	
		
		
	}
/*	public static void main(String[] args) { 
		
		ReadData test = new ReadData();

	}*/

}
