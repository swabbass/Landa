package ward.landa.ImageUtilities;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

	private final WeakReference<ImageView> imageViewReference;
	private int data = 0;
	BitmapUtils bmpUtils;

	public BitmapWorkerTask(ImageView imageView, Context cxt) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		imageViewReference = new WeakReference<ImageView>(imageView);
		bmpUtils = new BitmapUtils(cxt);
	}

	// Decode image in background.
	@Override
	protected Bitmap doInBackground(Integer... params) {
		setData(params[0]);
		return bmpUtils.decodeBitmapNoAllocation(getData(), 120, 120);
	}

	// Once complete, see if ImageView is still around and set bitmap.
	@Override
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}
		if (imageViewReference != null && bitmap != null) {
			final ImageView imageView = imageViewReference.get();
			final BitmapWorkerTask bitmapWorkerTask = BitmapUtils
					.getBitmapWorkerTask(imageView);
			if (this == bitmapWorkerTask && imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}
}