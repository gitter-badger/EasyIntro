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

package io.github.meness.easyintro.app;

import android.support.v4.app.Fragment;

import io.github.meness.easyintro.EasyIntro;

public class MyEasyIntro extends EasyIntro {
    @Override
    public void onSlide(Fragment fragment) {
        super.onSlide(fragment);
    }

    @Override
    public void onSkipClick(Fragment fragment) {
        super.onSkipClick(fragment);
    }

    @Override
    public void onPreviousClick(Fragment fragment) {
        super.onPreviousClick(fragment);
    }

    @Override
    public void onDoneClick(Fragment fragment) {
        super.onDoneClick(fragment);
    }

    @Override
    public void onNextClick(Fragment fragment) {
        super.onNextClick(fragment);
    }

    @Override
    protected void init() {
        withSlide(IntroOneFragment.instantiate(getApplicationContext(), IntroOneFragment.class.getName()));
        withSlide(IntroTwoFragment.instantiate(getApplicationContext(), IntroTwoFragment.class.getName()));
        withSlide(IntroOneFragment.instantiate(getApplicationContext(), IntroOneFragment.class.getName()));
    }
}
