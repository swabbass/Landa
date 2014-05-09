package ward.landa;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Fx {

	public static void slideDown(Context cxt,View v)
	{
	
		Animation a=AnimationUtils.loadAnimation(cxt, R.animator.slide_down);
		if(a!=null)
		{
			a.reset();
		}
		if(v!=null)
		{
			v.clearAnimation();
			v.startAnimation(a);
		}
	}
	public static void slideDown(Context cxt,View v,int anim)
	{
	
		Animation a=AnimationUtils.loadAnimation(cxt, anim);
		if(a!=null)
		{
			a.reset();
		}
		if(v!=null)
		{
			v.clearAnimation();
			v.startAnimation(a);
		}
	}
	public static void slideUp(Context cxt,View v,int anime)
	{
	
		Animation a=AnimationUtils.loadAnimation(cxt,anime);
		if(a!=null)
		{
			a.reset();
		}
		if(v!=null)
		{
			v.clearAnimation();
			v.startAnimation(a);
		}
	}
	public static void slideUp(Context cxt,View v)
	{
	
		Animation a=AnimationUtils.loadAnimation(cxt, R.animator.slideup);
		if(a!=null)
		{
			a.reset();
		}
		if(v!=null)
		{
			v.clearAnimation();
			v.startAnimation(a);
		}
	}
}
