package ward.landa.ImageUtilities;

import ward.landa.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class UILTools {
	public static ImageLoaderConfiguration initilizeImageLoader(
			DisplayImageOptions options, Context cxt) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				cxt).memoryCacheExtraOptions(150, 150)
				.denyCacheImageMultipleSizesInMemory()
				.threadPriority(Thread.MAX_PRIORITY)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).threadPoolSize(5)
				.defaultDisplayImageOptions(options).tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.writeDebugLogs().build();
		return config;

	}

	public static DisplayImageOptions initlizeImageDisplay(int onLoading,
			int empty, int fail) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(onLoading)
				// resource or drawable
				.showImageForEmptyUri(empty)
				// resource or drawable
				.showImageOnFail(fail)
				// resource or drawable
				.resetViewBeforeLoading(false)
				// default
				.resetViewBeforeLoading(true)
				.delayBeforeLoading(200).cacheInMemory(true).cacheOnDisc(true)
				
				.considerExifParams(false) // default
				.imageScaleType(ImageScaleType.EXACTLY) // default
				.bitmapConfig(Bitmap.Config.ARGB_8888) // default
				.handler(new Handler()) // default
				.build();
		return options;
	}

	public static ImageLoader initlizeImageLoad(
			ImageLoaderConfiguration config, Context cxt) {
		config = UILTools.initilizeImageLoader(UILTools.initlizeImageDisplay(
				R.drawable.person, R.drawable.person, R.drawable.person), cxt);
		ImageLoader image_loader = ImageLoader.getInstance();
		image_loader.init(config);
		boolean pauseOnScroll = false; // or true
		boolean pauseOnFling = true; // or false
		PauseOnScrollListener listener = new PauseOnScrollListener(
				image_loader, pauseOnScroll, pauseOnFling);
		return image_loader;
	}
}
