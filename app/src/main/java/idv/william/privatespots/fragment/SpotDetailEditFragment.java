package idv.william.privatespots.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import idv.william.privatespots.MainActivity;
import idv.william.privatespots.R;
import idv.william.privatespots.bean.Spot;

public class SpotDetailEditFragment extends Fragment {
	private static final String TAG = "TAG_EditFragment";
	private MainActivity activity;
	private ImageButton ibSave;
	private EditText etTitle, etDesc;
	private RecyclerView rvImages;
	private TextView tvCreatedDate;
	private SpotAdapter adapter;
	private ActivityResultLauncher<Uri> takePicLauncher;
	private ImageView currImageView;
	private File file;
	private String currImageTag;
	private Map<String, String> images;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = (MainActivity) getActivity();
		adapter = new SpotAdapter(activity);
		takePicLauncher = activity.registerForActivityResult(new ActivityResultContracts.TakePicture(), isOk -> adapter.afterTakePicture(isOk));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_spot_detail_edit, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		findViews(view);
		handleRcImages();
		handleTvCreatedDate();
		handleIbSave();
		showSpot();
	}

	private void findViews(View view) {
		ibSave = view.findViewById(R.id.ibEdit);
		etTitle = view.findViewById(R.id.etTitle);
		etDesc = view.findViewById(R.id.tvDesc);
		rvImages = view.findViewById(R.id.rvImages);
		tvCreatedDate = view.findViewById(R.id.tvCreatedDate);
	}

	private void handleRcImages() {
		rvImages.setAdapter(new SpotAdapter(activity));
		rvImages.setLayoutManager(new GridLayoutManager(activity, 2));
	}

	private void handleTvCreatedDate() {
		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH) + 1;
		final int day = calendar.get(Calendar.DAY_OF_MONTH);
		tvCreatedDate.setText(
				new StringBuilder()
						.append(year)
						.append("年")
						.append(month)
						.append("月")
						.append(day)
						.append("日")
		);
	}

	private void handleIbSave() {
		ibSave.setOnClickListener(view -> {
			final String title = String.valueOf(etTitle.getText());
			final String desc = String.valueOf(etDesc.getText());
			if (title.isEmpty()) {
				etTitle.setError("請輸入標題");
				return;
			}
			if (desc.isEmpty()) {
				etDesc.setError("請輸入描述");
				return;
			}
			final String createdDate = String.valueOf(tvCreatedDate.getText());
			final Spot spot = new Spot(1, title, images, desc, createdDate);
			try (
					FileOutputStream fos = activity.openFileOutput("SPOTS", MODE_PRIVATE);
					ObjectOutputStream oos = new ObjectOutputStream(fos)
			) {
				oos.writeObject(spot);
				Toast.makeText(activity, "存檔成功", Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
		});
	}

	private void showSpot() {
		try (
				FileInputStream fis = activity.openFileInput("SPOTS");
				ObjectInputStream ois = new ObjectInputStream(fis);
		) {
			final Spot spot = (Spot) ois.readObject();
			if (spot == null) {
				return;
			}
			etTitle.setText(spot.getTitle());
			etDesc.setText(spot.getDesc());
			tvCreatedDate.setText(spot.getCreatedDate());
			images = spot.getImages();
		} catch (IOException | ClassNotFoundException e) {
			Log.e(TAG, e.toString());
		}
	}

	private class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {
		Context context;
		int no;

		public SpotAdapter(Context context) {
			this.context = context;
		}

		private class SpotViewHolder extends RecyclerView.ViewHolder {
			ImageView imageView;

			public SpotViewHolder(@NonNull View itemView) {
				super(itemView);
				imageView = itemView.findViewById(R.id.imageView);
				imageView.setTag("image" + no++);
			}
		}

		@Override
		public int getItemCount() {
			return 4;
		}

		@NonNull
		@Override
		public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.item_view_image, parent, false);
			return new SpotViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
			holder.imageView.setOnClickListener(view -> {
				try {
					currImageTag = (String) view.getTag();
					file = createImageFile();
					Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
					currImageView = holder.imageView;
					takePicLauncher.launch(uri);
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			});

			if (images == null) {
				return;
			}
			String imageStr = images.get("image" + position);
			if (imageStr == null) {
				return;
			}
			final File file = new File(imageStr);
			Bitmap bitmap = null;
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
				bitmap = BitmapFactory.decodeFile(file.getPath());
			} else {
				ImageDecoder.Source source = ImageDecoder.createSource(file);
				try {
					bitmap = ImageDecoder.decodeBitmap(source);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			holder.imageView.setImageBitmap(bitmap);
		}

		private void afterTakePicture(boolean isOk) {
			if (!isOk) {
				return;
			}
			Bitmap bitmap = null;
			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.P) {
				bitmap = BitmapFactory.decodeFile(file.getPath());
			} else {
				try {
					ImageDecoder.Source source = ImageDecoder.createSource(file);
					bitmap = ImageDecoder.decodeBitmap(source);
				} catch (IOException e) {
					Log.e(TAG, e.toString());
				}
			}
			currImageView.setBackground(null);
			currImageView.setImageBitmap(bitmap);
			if (images == null) {
				images = new HashMap<>();
			}
			images.put(currImageTag, file.getAbsolutePath());
		}

		private File createImageFile() throws IOException {
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
			String imageFileName = "JPEG_" + timeStamp + "_";
			File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
			return File.createTempFile(imageFileName, ".jpg", storageDir);
		}
	}
}