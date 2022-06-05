package idv.william.privatespots.fragment;

import static idv.william.privatespots.common.Constant.KEY_ACTION;
import static idv.william.privatespots.common.Constant.KEY_SPOT;

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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import idv.william.privatespots.R;
import idv.william.privatespots.bean.Spot;
import idv.william.privatespots.common.Action;

public class SpotDetailDisplayFragment extends Fragment {
	private static final String TAG = "TAG_DetailDisplay";
	private Activity activity;
	private TextView tvTitle, tvDesc, tvCreatedDate;
	private ImageButton ibEdit;
	private RecyclerView rvImages;
	private Spot spot;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if (bundle != null) {
			spot = (Spot) bundle.getSerializable("SPOT");
		}
		return inflater.inflate(R.layout.fragment_spot_detail_display, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = getActivity();
		findViews(view);
		handleTextViews();
		handleIbEdit();
		handleRvImages();
	}

	private void findViews(View view) {
		tvTitle = view.findViewById(R.id.tvTitle);
		ibEdit = view.findViewById(R.id.ibEdit);
		rvImages = view.findViewById(R.id.rvImages);
		tvDesc = view.findViewById(R.id.tvDesc);
		tvCreatedDate = view.findViewById(R.id.tvCreatedDate);
	}

	private void handleTextViews() {
		if (spot != null) {
			tvTitle.setText(spot.getTitle());
			tvDesc.setText(spot.getDesc());
			tvCreatedDate.setText(spot.getCreatedDate());
		}
	}

	private void handleIbEdit() {
		ibEdit.setOnClickListener(view -> {
			Bundle bundle = new Bundle();
			bundle.putSerializable(KEY_SPOT, spot);
			bundle.putSerializable(KEY_ACTION, Action.EDIT);
			Navigation.findNavController(view).navigate(R.id.actionDetailDisplayToDetailEdit, bundle);
		});
	}

	private void handleRvImages() {
		rvImages.setAdapter(new SpotAdapter(activity, spot.getImages()));
		rvImages.setLayoutManager(new GridLayoutManager(activity, 2));
	}

	private static class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {
		Context context;
		Map<String, String> images;

		public SpotAdapter(Context context, Map<String, String> images) {
			this.context = context;
			this.images = images;
		}

		private static class SpotViewHolder extends RecyclerView.ViewHolder {
			ImageView imageView;

			public SpotViewHolder(@NonNull View itemView) {
				super(itemView);
				imageView = itemView.findViewById(R.id.imageView);
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
			if (images == null) {
				return;
			}
			String imageStr = images.get("image" + position);
			if (imageStr == null) {
				return;
			}
			File file = new File(imageStr);
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
			holder.imageView.setBackground(null);
			holder.imageView.setImageBitmap(bitmap);

			holder.imageView.setOnClickListener(view -> {
				ImageView imageView = new ImageView(context);
				imageView.setImageDrawable(holder.imageView.getDrawable());
				Toast toast = new Toast(context);
				toast.setView(imageView);
				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();

			});
		}
	}
}