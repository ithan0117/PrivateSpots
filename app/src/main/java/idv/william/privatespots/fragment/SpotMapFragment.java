package idv.william.privatespots.fragment;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static idv.william.privatespots.common.Constant.FILENAME;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;

import idv.william.privatespots.R;
import idv.william.privatespots.bean.Spot;
import idv.william.privatespots.util.InternalStorageUtil;

public class SpotMapFragment extends Fragment {
	private Activity activity;
	private MapView mapView;
	private GoogleMap googleMap;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_spot_map, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		activity = getActivity();
		findViews(view);
		handleMapView(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		requestPermissionLauncher.launch(ACCESS_FINE_LOCATION);
	}

	private void findViews(View view) {
		mapView = view.findViewById(R.id.mapView);
	}

	private void handleMapView(Bundle savedInstanceState) {
		mapView.onCreate(savedInstanceState);
		mapView.onStart();
		mapView.getMapAsync(googleMap -> {
			this.googleMap = googleMap;
			final List<Spot> spots = InternalStorageUtil.read(activity, FILENAME);
			if (spots != null) {
				for (Spot spot : spots) {
					addMarker(spot);
				}
			}
			googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter(activity));
			showMyLocation();
		});
	}

	private void showMyLocation() {
		if (ActivityCompat.checkSelfPermission(activity, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(activity, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		googleMap.setMyLocationEnabled(true);
		FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
		Task<Location> task = fusedLocationClient.getLastLocation();
		task.addOnSuccessListener(location -> {
			if (location != null) {
				final double lat = location.getLatitude();
				final double lng = location.getLongitude();
				CameraPosition cameraPosition = new CameraPosition.Builder()
						.target(new LatLng(lat, lng))
						.zoom(8)
						.build();
				CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
				googleMap.animateCamera(cameraUpdate);
			}
		});
	}

	private void addMarker(final Spot spot) {
		Marker marker = googleMap.addMarker(new MarkerOptions()
				.position(new LatLng(spot.getLat(), spot.getLng()))
				.title(spot.getTitle())
				.snippet(spot.getDesc())
		);
//		marker.setTag(spot.getImages().get("image0"));
	}

	private static class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
		Context context;

		public MyInfoWindowAdapter(Context context) {
			this.context = context;
		}

		@Override
		public View getInfoWindow(@NonNull Marker marker) {
			View view = View.inflate(context, R.layout.info_window, null);
			final TextView tvTitle = view.findViewById(R.id.tvTitle);
			final TextView tvSnippet = view.findViewById(R.id.tvSnippet);
//			final ImageView imageView = view.findViewById(R.id.imageView);
			tvTitle.setText(marker.getTitle());
			tvSnippet.setText(marker.getSnippet());
//			imageView.setImageBitmap(getBitmapFromFile((String) marker.getTag()));
			return view;
		}

		@Override
		public View getInfoContents(@NonNull Marker marker) {
			return null;
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