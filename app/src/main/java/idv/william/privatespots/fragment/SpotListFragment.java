package idv.william.privatespots.fragment;

import static idv.william.privatespots.common.Action.ADD;
import static idv.william.privatespots.common.Constant.FILENAME;
import static idv.william.privatespots.common.Constant.KEY_ACTION;
import static idv.william.privatespots.common.Constant.KEY_SPOT;
import static idv.william.privatespots.util.InternalStorageUtil.read;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
		handleIbSave();
		handleRvImages();
		// TODO just for testing
		view.findViewById(R.id.cvTitle).setOnClickListener(v -> {
			Navigation.findNavController(v).navigate(R.id.spotMapFragment);
		});
	}

	private void findViews(View view) {
		ibSave = view.findViewById(R.id.ibEdit);
		rvImages = view.findViewById(R.id.rvImages);
	}

	private void handleIbSave() {
		ibSave.setOnClickListener(view -> {
			Bundle bundle = new Bundle();
			bundle.putSerializable(KEY_ACTION, ADD);
			Navigation.findNavController(view).navigate(R.id.actionListToDetailEdit, bundle);
		});
	}

	private void handleRvImages() {
		final List<Spot> spots = read(activity, FILENAME);
		rvImages.setAdapter(new SpotAdapter(activity, spots));
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
				bundle.putSerializable(KEY_SPOT, spot);
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