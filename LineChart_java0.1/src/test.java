import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class test {
	public static void main(String[] args) {
		File f = new File("F:\\test.txt");
		try {
			String str = "test:";
			BufferedWriter write = new BufferedWriter(new FileWriter(f));
			for(int i=0; i<1000000000; i++){
				write.write(str+i);
				
			}
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
