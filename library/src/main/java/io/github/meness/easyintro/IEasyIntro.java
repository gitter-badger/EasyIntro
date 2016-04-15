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
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;

import io.github.meness.easyintro.enums.SlideTransformer;
import io.github.meness.easyintro.enums.ToggleIndicators;

interface IEasyIntro {
    void withTranslucentStatusBar(boolean b);

    void withHidePageIndicator();

    void withShowPageIndicator();

    void withStatusBarColor(@ColorInt int statusBarColor);

    void withNextSlide();

    void withPreviousSlide();

    void withSlideTo(int page);

    Fragment getCurrentSlide();

    void withOffScreenPageLimit(int limit);

    void withTransparentStatusBar(boolean b);

    void withTransparentNavigationBar(boolean b);

    void withFullscreen(boolean b);

    void withTranslucentNavigationBar(boolean b);

    void withSlide(Fragment fragment);

    void withSlideTransformer(SlideTransformer transformer);

    boolean isRightIndicatorVisible();

    void withRightIndicatorDisabled(boolean b);

    boolean isRightIndicatorDisabled();

    void withRemoveSlide(Class<? extends Fragment> aClass);

    boolean isLeftIndicatorVisible();

    void withLeftIndicatorDisabled(boolean b);

    boolean isLeftIndicatorDisabled();

    void withToggleIndicators(ToggleIndicators indicators);

    void withSmoothScroll(boolean b);

    boolean isSmoothScrollEnabled();

    void withVibrateOnSlide(int intensity);

    void withVibrateOnSlide();

    void withRtlSwipe();

    void withPageMargin(int marginPixels);

    int getPageMargin();

    void setPageMarginDrawable(Drawable d);

    void setPageMarginDrawable(@DrawableRes int resId);

    void withToggleIndicatorsSound(boolean b);

    void getIndicatorsContainerHeight(IndicatorsContainerHeight containerHeight);
}
