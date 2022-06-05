package idv.william.privatespots.fragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static idv.william.privatespots.common.Constant.FILENAME;
import static idv.william.privatespots.util.InternalStorageUtil.read;
import static idv.william.privatespots.util.InternalStorageUtil.save;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import idv.william.privatespots.MainActivity;
import idv.william.privatespots.R;
import idv.william.privatespots.bean.Spot;
import idv.william.privatespots.common.Action;

public class SpotDetailEditFragment extends Fragment {
	private static final String TAG = "TAG_EditFragment";
	private Action action;
	private Spot spot;
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
	private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
			new ActivityResultContracts.RequestPermission(),
			result -> {
				if (result) {
					showMyLocation();
				}
			});

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			spot = (Spot) bundle.get("SPOT");
			spot = spot == null ? new Spot() : spot;
			action = (Action) bundle.get("ACTION");
		}
		return inflater.inflate(R.layout.fragment_spot_detail_edit, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = (MainActivity) getActivity();
		adapter = new SpotAdapter(activity);
		if (action == Action.EDIT && spot != null) {
			adapter.images = spot.getImages();
		}
		takePicLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), isOk -> adapter.afterTakePicture(isOk));
		findViews(view);
		handleIbSave();
		handleEditTexts();
		handleRcImages();
		handleTvCreatedDate();
	}

	@Override
	public void onStart() {
		super.onStart();
		requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
	}

	private void findViews(View view) {
		ibSave = view.findViewById(R.id.ibEdit);
		etTitle = view.findViewById(R.id.etTitle);
		etDesc = view.findViewById(R.id.tvDesc);
		rvImages = view.findViewById(R.id.rvImages);
		tvCreatedDate = view.findViewById(R.id.tvCreatedDate);
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
			List<Spot> spots = read(activity, FILENAME);
			if (spots == null) {
				spots = new ArrayList<>();
			}
			spot.setTitle(title);
			spot.setImages(adapter.images);
			spot.setDesc(desc);
			spot.setCreatedDate(String.valueOf(tvCreatedDate.getText()));
			switch (action) {
				case ADD:
					spot.setId(spots.size());
					spots.add(spot);
					break;
				case EDIT:
					for (int index = 0; index < spots.size(); index++) {
						final Spot tmp = spots.get(index);
						if (Objects.equals(tmp.getId(), spot.getId())) {
							spots.set(index, spot);
							break;
						}
					}
					break;
			}
			final boolean result = save(activity, FILENAME, spots);
			Toast.makeText(activity, result ? "存檔成功" : "存檔失敗", Toast.LENGTH_SHORT).show();
		});
	}

	private void handleEditTexts() {
		if (action == Action.EDIT && spot != null) {
			etTitle.setText(spot.getTitle());
			etDesc.setText(spot.getDesc());
		}
	}

	private void handleRcImages() {
		rvImages.setAdapter(adapter);
		rvImages.setLayoutManager(new GridLayoutManager(activity, 2));
	}

	private void handleTvCreatedDate() {
		switch (action) {
			case ADD:
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
				break;
			case EDIT:
				if (spot != null) {
					tvCreatedDate.setText(spot.getCreatedDate());
				}
				break;
		}
	}

	private class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {
		Context context;
		Map<String, String> images;
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

			if (action == Action.ADD || images == null) {
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

	private void showMyLocation() {
		if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
		Task<Location> task = fusedLocationClient.getLastLocation();
		task.addOnSuccessListener(location -> {
			if (location != null) {
				final double lat = location.getLatitude();
				final double lng = location.getLongitude();
				spot.setLat(lat);
				spot.setLng(lng);
			}
		});
	}
}