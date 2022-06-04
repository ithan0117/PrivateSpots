package idv.william.privatespots.util;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InternalStorageUtil {
	private static final String TAG = "TAG_InternalStorageUtil";

	public static <T> boolean save(Context context, String filename, T data) {
		try (
				FileOutputStream fos = context.openFileOutput(filename, MODE_PRIVATE);
				ObjectOutputStream oos = new ObjectOutputStream(fos)
		) {
			oos.writeObject(data);
			return true;
		} catch (IOException e) {
			Log.e(TAG, e.toString());
			return false;
		}
	}

	public static <T> T read(Context context, String filename) {
		try (
				FileInputStream fis = context.openFileInput(filename);
				ObjectInputStream ois = new ObjectInputStream(fis)
		) {
			return (T) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			Log.e(TAG, e.toString());
			return null;
		}
	}
}
