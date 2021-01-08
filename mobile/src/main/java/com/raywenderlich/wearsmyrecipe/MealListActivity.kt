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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.wearable.Node
import androidx.recyclerview.widget.LinearLayoutManager
import com.byoyedele.shared.Meal
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.raywenderlich.wearsmyrecipe.databinding.ActivityMainBinding


class MealListActivity : AppCompatActivity(), MealListAdapter.Callback, GoogleApiClient.ConnectionCallbacks {
  private var adapter: MealListAdapter? = null
  private lateinit var client: GoogleApiClient
  private var connectedNode: List<Node>? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val binding = ActivityMainBinding.inflate(layoutInflater)

    client = GoogleApiClient.Builder(this)
            .addApi(Wearable.API)
            .addConnectionCallbacks(this)
            .build()
    client.connect()

    val meals = MealStore.fetchMeals(this)
    adapter = MealListAdapter(meals, this)
    binding.list.adapter = adapter
    binding.list.layoutManager = LinearLayoutManager(this)
    setContentView(binding.root)
  }

  override fun mealClicked(meal: Meal) {
    val gson = Gson()
    connectedNode?.forEach{
      val bytes = gson.toJson(meal).toByteArray()
      Wearable.MessageApi.sendMessage(client, it.id, "/meal", bytes)
    }
  }

  override fun onConnected(p0: Bundle?) {
    Wearable.NodeApi.getConnectedNodes(client).setResultCallback {
      connectedNode = it.nodes
    }
  }

  override fun onConnectionSuspended(p0: Int) {
    connectedNode = null
  }
}
