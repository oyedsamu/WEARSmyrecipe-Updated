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

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.byoyedele.shared.Meal
import com.raywenderlich.wearsmyrecipe.databinding.AdapterMealBinding

class MealListAdapter(
        private val meals: MutableList<Meal>,
        private val callback: Callback?
) : RecyclerView.Adapter<MealListAdapter.MealViewHolder>() {

  override fun onBindViewHolder(holder: MealViewHolder, position: Int) {

    val meal = meals[position]
    holder.bind(meal)
    holder.itemView.setOnClickListener {
      callback?.mealClicked(meal)
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
    val binding = AdapterMealBinding.inflate(LayoutInflater.from(parent.context))
    return MealViewHolder(binding)
  }

  override fun getItemCount() = meals.size

  fun updateMeal(meal: Meal) {
    for ((index, value) in meals.withIndex()) {
      if (value.title == meal.title) {
        meals[index] = meal
      }
    }

    notifyDataSetChanged()
  }

  inner class MealViewHolder(private val itemBinding: AdapterMealBinding) : RecyclerView.ViewHolder(itemBinding.root){
    fun bind(meal: Meal){
      itemBinding.title.text = meal.title
      itemBinding.ingredients.text = meal.ingredients.joinToString(separator = ", ")
      itemBinding.calories.text = meal.calories.toString()
      itemBinding.star.visibility = if (meal.favorited) View.VISIBLE else View.INVISIBLE
    }
  }

  interface Callback {
    fun mealClicked(meal: Meal)
  }
}