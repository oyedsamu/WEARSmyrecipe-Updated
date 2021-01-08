/*
 * Copyright (c) 2017 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.wearsmyrecipe

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.byoyedele.shared.Meal
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import android.support.wearable.activity.ConfirmationActivity
import com.raywenderlich.wearsmyrecipe.databinding.ActivityMealBinding

class MealActivity : Activity(), GoogleApiClient.ConnectionCallbacks {

  private lateinit var client: GoogleApiClient
  private var currentMeal: Meal? = null
  private var binding: ActivityMealBinding? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    client = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addApi(Wearable.API)
            .build()
    client.connect()
    binding!!.star.setOnClickListener {
      sendLike()
    }

    binding = ActivityMealBinding.inflate(layoutInflater)
    setContentView(binding!!.root)
  }

  override fun onConnected(p0: Bundle?) {
    Wearable.MessageApi.addListener(client){
      currentMeal = Gson().fromJson(String(it.data), Meal::class.java)
      updateView()
    }
  }

  private fun updateView() {
    currentMeal?.let {
      binding!!.mealTitle.text = it.title
      binding!!.calories.text = getString(R.string.calories, it.calories)
      binding!!.ingredients.text = it.ingredients.joinToString(separator = " | ")

    }
  }

  private fun sendLike() {
    currentMeal?.let {
      val bytes = Gson().toJson(it.copy(favorited = true)).toByteArray()
      Wearable.DataApi.putDataItem(client, PutDataRequest.create("/liked").setData(bytes).setUrgent()).setResultCallback {
        showConfirmationScreen()
      }
    }
  }

  override fun onConnectionSuspended(p0: Int) {
    Log.w("Wear", "Google Api Client connection suspended!")
  }

  private fun showConfirmationScreen() {
    val intent = Intent(this, ConfirmationActivity::class.java)
    intent.putExtra(
            ConfirmationActivity.EXTRA_ANIMATION_TYPE,
            ConfirmationActivity.SUCCESS_ANIMATION
    )
    intent.putExtra(
            ConfirmationActivity.EXTRA_MESSAGE,
            getString(R.string.starred_meal)
    )
    startActivity(intent)
  }
}
