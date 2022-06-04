package idv.william.privatespots.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import idv.william.privatespots.R;
import idv.william.privatespots.bean.Spot;

public class SpotListFragment extends Fragment {
	private static final String TAG = "TAG_ListFragment";
	private Activity activity;
	private ImageButton ibSave;
	private RecyclerView rvImages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_spot_list, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = getActivity();
		findViews(view);
		handleRvImages();
	}

	private void findViews(View view) {
		ibSave = view.findViewById(R.id.ibEdit);
		rvImages = view.findViewById(R.id.rvImages);
	}

	private void handleRvImages() {
		Map<String, String> images = new HashMap<>();
		images.put("image0", "/storage/emulated/0/Android/data/idv.william.privatespots/files/Pictures/JPEG_20220604_164352_2491260279204968104.jpg");
		images.put("image1", "/storage/emulated/0/Android/data/idv.william.privatespots/files/Pictures/JPEG_20220604_164416_2785517651206787370.jpg");
		images.put("image2", "/storage/emulated/0/Android/data/idv.william.privatespots/files/Pictures/JPEG_20220604_164455_7678704254314877917.jpg");
		images.put("image3", "/storage/emulated/0/Android/data/idv.william.privatespots/files/Pictures/JPEG_20220604_164534_2561553517156697525.jpg");
		final List<Spot> list = Arrays.asList(
				new Spot(0, "aaaaaaaaaaaaaaaa", images, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", "1月2號"),
				new Spot(1, "bbbbbbbbbbbbbbbb", images, "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb", "3月4號"),
				new Spot(2, "cccccccccccccccc", images, "cccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccccc", "5月6號"),
				new Spot(3, "dddddddddddddddd", images, "dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd", "7月8號"),
				new Spot(4, "eeeeeeeeeeeeeeee", images, "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee", "9月10號")
		);
		rvImages.setAdapter(new SpotAdapter(activity, list));
		rvImages.setLayoutManager(new LinearLayoutManager(activity));
	}

	private static class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {
		Context context;
		List<Spot> list;

		public SpotAdapter(Context context, List<Spot> list) {
			this.context = context;
			this.list = list;
		}

		private static class SpotViewHolder extends RecyclerView.ViewHolder {
			ImageView imageView;
			TextView tvTitle, tvDesc;

			public SpotViewHolder(@NonNull View itemView) {
				super(itemView);
				imageView = itemView.findViewById(R.id.imageView);
				tvTitle = itemView.findViewById(R.id.tvTitle);
				tvDesc = itemView.findViewById(R.id.tvDesc);
			}
		}

		@Override
		public int getItemCount() {
			return list == null ? 0 : list.size();
		}

		@NonNull
		@Override
		public SpotAdapter.SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
			return new SpotAdapter.SpotViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(@NonNull SpotAdapter.SpotViewHolder holder, int position) {
			final Spot spot = list.get(position);
			holder.itemView.setOnClickListener(view -> {
				Bundle bundle = new Bundle();
				bundle.putSerializable("SPOT", spot);
				Navigation.findNavController(view).navigate(R.id.actionListToDetailDisplay, bundle);

			});
			holder.tvTitle.setText(spot.getTitle());
			holder.tvDesc.setText(spot.getDesc());

			Map<String, String> images = spot.getImages();
			if (images == null) {
				return;
			}
			final String imageStr = images.get("image0");
			if (imageStr == null) {
				return;
			}
			holder.imageView.setImageBitmap(getBitmapFromFile(imageStr));
		}

		private Bitmap getBitmapFromFile(String imageStr) {
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
			return bitmap;
		}
	}
}