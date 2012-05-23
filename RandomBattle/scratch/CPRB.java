import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class CPRB
{
	public static void main(String[] args)
	{
		File exported =
		        new File(
		                "C:\\Users\\Merdril\\learning-to-program\\Java\\Eclipse\\RandomBattle\\export\\RandomBattle.jar");
		File svn =
		        new File(
		                "C:\\Users\\Merdril\\learning-to-program\\Java\\Eclipse\\TestServer\\plugins\\RandomBattle.jar");
		svn.delete();
		try {
			svn.createNewFile();
		}
		catch (IOException e) {
			System.out.println("Could not create the file. Aborting...");
			e.printStackTrace();
			System.exit(1);
		}
		FileChannel source = null;
		FileChannel dest = null;
		try {
			source = new FileInputStream(exported).getChannel();
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find the specified source file. Aborting...");
			e.printStackTrace();
			System.exit(2);
		}
		try {
			dest = new FileOutputStream(svn).getChannel();
		}
		catch (FileNotFoundException e) {
			System.out.println("Could not find the specified dest file. Aborting...");;
			try {
				source.close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
			System.exit(2);
		}
		try {
			dest.transferFrom(source, 0, source.size());
		}
		catch (IOException e) {
			System.out.println("Could not copy the file. Closing streams...");
			e.printStackTrace();
		}
		finally {
			try {
				dest.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				source.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Copied: " + exported.getAbsolutePath() + " to: " + svn.getAbsolutePath());
	}
}
