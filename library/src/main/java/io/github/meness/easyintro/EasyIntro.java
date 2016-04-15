/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.meness.easyintro;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.mikepenz.materialize.MaterializeBuilder;

import java.lang.reflect.InvocationTargetException;

import io.github.meness.easyintro.enums.PageIndicator;
import io.github.meness.easyintro.enums.SlideTransformer;
import io.github.meness.easyintro.enums.SwipeDirection;
import io.github.meness.easyintro.enums.ToggleIndicators;
import io.github.meness.easyintro.listeners.OnDoneClickListener;
import io.github.meness.easyintro.listeners.OnNextClickListener;
import io.github.meness.easyintro.listeners.OnPreviousClickListener;
import io.github.meness.easyintro.listeners.OnSkipClickListener;
import io.github.meness.easyintro.listeners.OnSlideListener;
import io.github.meness.easyintro.listeners.OnToggleIndicatorsClickListener;
import io.github.meness.easyintro.views.DirectionalViewPager;
import io.github.meness.easyintro.views.LeftToggleIndicator;
import io.github.meness.easyintro.views.RightToggleIndicator;

public abstract class EasyIntro extends AppCompatActivity implements IEasyIntro, OnToggleIndicatorsClickListener, OnSlideListener, OnNextClickListener, OnPreviousClickListener, OnDoneClickListener, OnSkipClickListener {
    private static final String TAG = EasyIntro.class.getSimpleName();
    private final EasyPagerAdapter mAdapter = new EasyPagerAdapter(getSupportFragmentManager());
    public ViewGroup mIndicatorsContainer;
    private DirectionalViewPager mPager;
    private MaterializeBuilder materializeBuilder;
    @RawRes
    private int mSoundRes;
    @LayoutRes
    private int mIndicatorRes;
    private RightToggleIndicator mRightIndicator;
    private LeftToggleIndicator mLeftIndicator;
    private ToggleIndicators mToggleIndicators = ToggleIndicators.DEFAULT;
    private boolean mSmoothScroll = true;
    private Vibrator mVibrator;
    private int mVibrateIntensity = 20;
    private boolean mVibrate;
    private boolean mRtlSwipe;

    @Override
    public void onSlide(Fragment fragment) {
        if (mSoundRes != 0) {
            MediaPlayer.create(getApplicationContext(), mSoundRes).start();
        }
        if (mVibrate) {
            mVibrator.vibrate(mVibrateIntensity);
        }

        updateToggleIndicators();
    }

    private void updateToggleIndicators() {
        if (mToggleIndicators == ToggleIndicators.NONE) {
            hideLeftIndicator();
            hideRightIndicator();
            return;
        }

        int slidesCount = mAdapter.getCount() - 1;
        int totalSlides = mAdapter.getCount();
        int currentItem = mPager.getCurrentItem();

        if (totalSlides == 0) {
            hideLeftIndicator();
            hideRightIndicator();
        } else if (totalSlides == 1) {
            hideLeftIndicator();
            showRightIndicator();
            mRightIndicator.makeItDone();
        } else {
            showRightIndicator();
            if (mToggleIndicators == ToggleIndicators.NO_LEFT_INDICATOR) {
                hideLeftIndicator();
            } else {
                showLeftIndicator();
            }
        }

        if (currentItem == slidesCount) {
            mRightIndicator.makeItDone();
            mLeftIndicator.makeItPrevious();
        } else if (currentItem < slidesCount) {
            if (currentItem == 0) {
                if (mToggleIndicators == ToggleIndicators.WITHOUT_SKIP || mToggleIndicators == ToggleIndicators.NO_LEFT_INDICATOR) {
                    hideLeftIndicator();
                } else {
                    mLeftIndicator.makeItSkip();
                    showLeftIndicator();
                }
            } else {
                if (mToggleIndicators == ToggleIndicators.NO_LEFT_INDICATOR) {
                    hideLeftIndicator();
                } else {
                    mLeftIndicator.makeItPrevious();
                    showLeftIndicator();
                }
            }
            mRightIndicator.makeItNext();
        }
    }

    private void hideLeftIndicator() {
        mLeftIndicator.hide();
    }

    private void hideRightIndicator() {
        mRightIndicator.hide();
    }

    private void showRightIndicator() {
        mRightIndicator.show();
    }

    private void showLeftIndicator() {
        mLeftIndicator.show();
    }

    @Override
    public final void onRightToggleClick() {
        int slidesCount = mAdapter.getCount() - 1;
        int currentItem = mPager.getCurrentItem();
        int nextItem = currentItem + 1;

        // we're on the last slide
        if (currentItem == slidesCount) {
            onDoneClick(mAdapter.getRegisteredFragment(currentItem));
        }
        // we're going to the last slide
        else if (nextItem == slidesCount) {
            mLeftIndicator.makeItPrevious();
            mRightIndicator.makeItDone();
            onNextClick(mAdapter.getRegisteredFragment(nextItem));
        }
        // we're going to the next slide
        else {
            mLeftIndicator.makeItPrevious();
            onNextClick(mAdapter.getRegisteredFragment(currentItem));
        }
    }

    @Override
    public final void onLeftToggleClick() {
        int currentItem = mPager.getCurrentItem();
        int previousItem = currentItem - 1;

        // we're on the first slide
        if (currentItem == 0) {
            onSkipClick(mAdapter.getRegisteredFragment(currentItem));
        }
        // we're going to the first slide
        else if (previousItem == 0) {
            mRightIndicator.makeItNext();
            mLeftIndicator.makeItSkip();
            onPreviousClick(mAdapter.getRegisteredFragment(previousItem));
        }
        // we're going to the previous slide
        else {
            mRightIndicator.makeItNext();
            mLeftIndicator.makeItPrevious();
            onPreviousClick(mAdapter.getRegisteredFragment(currentItem));
        }
    }

    @Override
    public void onSkipClick(Fragment fragment) {
        // empty
    }

    @Override
    public void onPreviousClick(Fragment fragment) {
        withPreviousSlide();
    }

    @Override
    public void onDoneClick(Fragment fragment) {
        // empty
    }

    @Override
    public void onNextClick(Fragment fragment) {
        withNextSlide();
    }

    /**
     * Set custom indicator. The indicator must have the `setViewPager(ViewPager)` method.
     * Indicator could be set once inside {@link #init()}.
     *
     * @param indicator Custom indicator resource
     */
    public final void withPageIndicator(@LayoutRes int indicator) {
        mIndicatorRes = indicator;
    }

    @Override
    public final void withTranslucentStatusBar(boolean b) {
        materializeBuilder.withTranslucentStatusBarProgrammatically(b);
    }

    @Override
    public final void withHidePageIndicator() {
        mIndicatorsContainer.findViewById(R.id.pageIndicator).setVisibility(View.GONE);
    }

    @Override
    public final void withShowPageIndicator() {
        mIndicatorsContainer.findViewById(R.id.pageIndicator).setVisibility(View.VISIBLE);
    }

    @Override
    public final void withStatusBarColor(@ColorInt int statusBarColor) {
        materializeBuilder.withStatusBarColor(statusBarColor);
    }

    @Override
    public final void withNextSlide() {
        mPager.setCurrentItem(mPager.getCurrentItem() + 1, mSmoothScroll);
    }

    @Override
    public final void withPreviousSlide() {
        mPager.setCurrentItem(mPager.getCurrentItem() - 1, mSmoothScroll);
    }

    @Override
    public final void withSlideTo(int page) {
        mPager.setCurrentItem(page, mSmoothScroll);
    }

    @Override
    public final Fragment getCurrentSlide() {
        return mAdapter.getRegisteredFragment(mPager.getCurrentItem());
    }

    @Override
    public final void withOffScreenPageLimit(int limit) {
        mPager.setOffscreenPageLimit(limit);
    }

    @Override
    public final void withTransparentStatusBar(boolean b) {
        materializeBuilder.withTransparentStatusBar(b);
    }

    @Override
    public final void withTransparentNavigationBar(boolean b) {
        materializeBuilder.withTransparentNavigationBar(b);
    }

    @Override
    public final void withFullscreen(boolean b) {
        materializeBuilder.withSystemUIHidden(b);
    }

    @Override
    public final void withTranslucentNavigationBar(boolean b) {
        materializeBuilder.withTranslucentNavigationBarProgrammatically(b);
    }

    @Override
    public final void withSlide(Fragment fragment) {
        mAdapter.addFragment(fragment);
    }

    @Override
    public final void withSlideTransformer(SlideTransformer transformer) {
        mPager.setPageTransformer(true, transformer.getTransformer());
    }

    @Override
    public final boolean isRightIndicatorVisible() {
        return mRightIndicator.isVisible();
    }

    @Override
    public final void withRightIndicatorDisabled(boolean b) {
        mRightIndicator.withDisabled(b);
    }

    @Override
    public final boolean isRightIndicatorDisabled() {
        return mRightIndicator.isDisabled();
    }

    @Override
    public final void withRemoveSlide(Class<? extends Fragment> aClass) {
        mAdapter.removeFragment(aClass);
    }

    @Override
    public final boolean isLeftIndicatorVisible() {
        return mLeftIndicator.isVisible();
    }

    @Override
    public final void withLeftIndicatorDisabled(boolean b) {
        mLeftIndicator.withDisabled(b);
    }

    @Override
    public final boolean isLeftIndicatorDisabled() {
        return mLeftIndicator.isDisabled();
    }

    @Override
    public final void withToggleIndicators(ToggleIndicators indicators) {
        mToggleIndicators = indicators;

        // RTL swipe support
        if (mRtlSwipe && indicators.getSwipeDirection() == SwipeDirection.LEFT) {
            withSwipeDirection(SwipeDirection.RIGHT);
        } else {
            withSwipeDirection(indicators.getSwipeDirection());
        }
    }

    @Override
    public final void withSmoothScroll(boolean b) {
        mSmoothScroll = b;
    }

    @Override
    public final boolean isSmoothScrollEnabled() {
        return mSmoothScroll;
    }

    @Override
    /**
     * @param intensity Desired intensity
     */
    public final void withVibrateOnSlide(int intensity) {
        setVibrateEnabled();
        mVibrateIntensity = intensity;
    }

    @Override
    /**
     * Enable vibration on slide with default 20 vibration intensity.
     * Needs {@link android.Manifest.permission#VIBRATE} permission
     *
     * @see EasyIntro#withVibrateOnSlide(int)
     */
    public final void withVibrateOnSlide() {
        setVibrateEnabled();
        mVibrate = true;
    }

    @Override
    public final void withRtlSwipe() {
        mRtlSwipe = true;
    }

    @Override
    /**
     * @see ViewPager#setPageMargin(int)
     */
    public final void withPageMargin(int marginPixels) {
        mPager.setPageMargin(marginPixels);
    }

    @Override
    public final int getPageMargin() {
        return mPager.getPageMargin();
    }

    @Override
    /**
     * @see ViewPager#setPageMarginDrawable(Drawable)
     */
    public final void setPageMarginDrawable(Drawable d) {
        mPager.setPageMarginDrawable(d);
    }

    @Override
    /**
     * @see ViewPager#setPageMarginDrawable(int)
     */
    public final void setPageMarginDrawable(@DrawableRes int resId) {
        mPager.setPageMarginDrawable(resId);
    }

    @Override
    public final void withToggleIndicatorsSound(boolean b) {
        mLeftIndicator.setSoundEffectsEnabled(b);
        mRightIndicator.setSoundEffectsEnabled(b);
    }

    @Override
    public final void getIndicatorsContainerHeight(final IndicatorsContainerHeight containerHeight) {
        mIndicatorsContainer.post(new Runnable() {
            @Override
            public void run() {
                containerHeight.call(mIndicatorsContainer.getHeight());
            }
        });
    }

    private void setVibrateEnabled() {
        if (!AndroidUtils.hasVibratePermission(getApplicationContext())) {
            Log.d(TAG, getString(R.string.exception_permission_vibrate));
            return;
        }
        mVibrate = true;
    }

    private void withSwipeDirection(SwipeDirection direction) {
        mPager.setAllowedSwipeDirection(direction);
    }

    /**
     * Play a sound while sliding.
     * Pass 0 for no sound (default)
     *
     * @param sound Sound raw resource
     */
    public final void withSlideSound(@RawRes int sound) {
        mSoundRes = sound;
    }

    /**
     * @see android.view.View#setOverScrollMode(int)
     */
    public final void withOverScrollMode(int mode) {
        mPager.setOverScrollMode(mode);
    }

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager);

        // native init
        materializeBuilder = new MaterializeBuilder(this);
        mPager = (DirectionalViewPager) findViewById(R.id.pager);
        mIndicatorsContainer = (ViewGroup) findViewById(R.id.indicatorsContainer);
        mRightIndicator = (RightToggleIndicator) findViewById(R.id.nextIndicator);
        mLeftIndicator = (LeftToggleIndicator) findViewById(R.id.previousIndicator);
        mVibrator = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);

        // user init
        init();

        // finalize
        materializeBuilder.build();
        mPager.addOnPageChangeListener(new OnPageChangeListener());
        mRightIndicator.setListener(EasyIntro.this);
        mLeftIndicator.setListener(EasyIntro.this);
        mPager.setAdapter(mAdapter);
        addIndicator();
        updateToggleIndicators();
    }

    protected abstract void init();

    private void addIndicator() {
        // circle indicator by default
        if (mIndicatorRes == 0) {
            withPageIndicator(PageIndicator.CIRCLE);
        }
        ViewStub viewStub = (ViewStub) mIndicatorsContainer.findViewById(R.id.pageIndicator);
        viewStub.setLayoutResource(mIndicatorRes);
        // id must be set
        viewStub.inflate().setId(R.id.pageIndicator);
        setViewPagerToPageIndicator();
    }

    /**
     * Set predefined indicator.
     * Indicator could be set once inside {@link #init()}.
     *
     * @param pageIndicator Custom indicator
     */
    public final void withPageIndicator(PageIndicator pageIndicator) {
        mIndicatorRes = pageIndicator.getIndicatorRes();
    }

    private void setViewPagerToPageIndicator() {
        try {
            View view = mIndicatorsContainer.findViewById(R.id.pageIndicator);
            // invoke indicator `setViewPager(ViewPager)` method
            view.getClass().getMethod("setViewPager", ViewPager.class).invoke(view, mPager);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, getString(R.string.exception_no_such_method_set_view_pager));
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            Log.e(TAG, getString(R.string.exception_invocation_target_set_view_pager));
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(TAG, getString(R.string.exception_illegal_access_set_view_pager));
            e.printStackTrace();
        }
    }

    private class OnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            onSlide(mAdapter.getItem(position));
        }
    }
}
