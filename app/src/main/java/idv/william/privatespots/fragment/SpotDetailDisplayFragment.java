package idv.william.privatespots.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Arrays;
import java.util.List;

import idv.william.privatespots.R;

public class SpotDetailDisplayFragment extends Fragment {
	private Activity activity;
	private RecyclerView rvImages;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_spot_detail_display, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = getActivity();
		findViews(view);
		handleRvImages();
	}

	private void findViews(View view) {
		rvImages = view.findViewById(R.id.rvImages);
	}

	private void handleRvImages() {
		rvImages.setAdapter(
				new SpotAdapter(
						activity,
						Arrays.asList(
								R.drawable.scenic1,
								R.drawable.scenic2,
								R.drawable.scenic3,
								R.drawable.scenic4,
								R.drawable.scenic5,
								R.drawable.scenic6
						)
				)
		);
		rvImages.setLayoutManager(new GridLayoutManager(activity, 2));
	}

	private static class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {
		Context context;
		List<Integer> list;

		public SpotAdapter(Context context, List<Integer> list) {
			this.context = context;
			this.list = list;
		}

		private static class SpotViewHolder extends RecyclerView.ViewHolder {
			ImageView ivImage;

			public SpotViewHolder(@NonNull View itemView) {
				super(itemView);
				ivImage = itemView.findViewById(R.id.ivImage);
			}
		}

		@Override
		public int getItemCount() {
			return list == null ? 0 : list.size();
		}

		@NonNull
		@Override
		public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View itemView = LayoutInflater.from(context).inflate(R.layout.image_item_view, parent, false);
			return new SpotViewHolder(itemView);
		}

		@Override
		public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
			final Integer image = list.get(position);
			holder.ivImage.setImageResource(image);
		}
	}
}