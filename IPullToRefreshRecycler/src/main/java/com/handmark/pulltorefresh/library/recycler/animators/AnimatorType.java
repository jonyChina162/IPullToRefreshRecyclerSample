package com.handmark.pulltorefresh.library.recycler.animators;

public enum AnimatorType {
	FadeIn("FadeIn", new FadeInAnimator()), FadeInDown("FadeInDown", new FadeInDownAnimator()), FadeInUp("FadeInUp",
			new FadeInUpAnimator()), FadeInLeft("FadeInLeft", new FadeInLeftAnimator()), FadeInRight("FadeInRight",
			new FadeInRightAnimator()), Landing("Landing", new LandingAnimator()), ScaleIn("ScaleIn",
			new ScaleInAnimator()), ScaleInTop("ScaleInTop", new ScaleInTopAnimator()), ScaleInBottom("ScaleInBottom",
			new ScaleInBottomAnimator()), ScaleInLeft("ScaleInLeft", new ScaleInLeftAnimator()), ScaleInRight(
			"ScaleInRight", new ScaleInRightAnimator()), FlipInTopX("FlipInTopX", new FlipInTopXAnimator()), FlipInBottomX(
			"FlipInBottomX", new FlipInBottomXAnimator()), FlipInLeftY("FlipInLeftY", new FlipInLeftYAnimator()), FlipInRightY(
			"FlipInRightY", new FlipInRightYAnimator()), SlideInLeft("SlideInLeft", new SlideInLeftAnimator()), SlideInRight(
			"SlideInRight", new SlideInRightAnimator()), SlideInDown("SlideInDown", new SlideInDownAnimator()), SlideInUp(
			"SlideInUp", new SlideInUpAnimator()), OvershootInRight("OvershootInRight", new OvershootInRightAnimator()), OvershootInLeft(
			"OvershootInLeft", new OvershootInLeftAnimator());

	private String mTitle;
	private BaseItemAnimator mAnimator;

	AnimatorType(String title, BaseItemAnimator animator) {
		mTitle = title;
		mAnimator = animator;
	}

	public BaseItemAnimator getAnimator() {
		return mAnimator;
	}

	public String getTitle() {
		return mTitle;
	}
}
