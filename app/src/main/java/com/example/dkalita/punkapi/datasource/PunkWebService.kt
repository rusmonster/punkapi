package com.example.dkalita.punkapi.datasource

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.net.URL
import javax.inject.Inject

class PunkWebService @Inject constructor() {

	fun fetchItem(id: Int): PunkItem = fetchList("https://api.punkapi.com/v2/beers/$id").first()

	fun fetchAll(): List<PunkItem> = fetchList("https://api.punkapi.com/v2/beers")

	private fun fetchList(url: String): List<PunkItem> {
		val text = URL(url).readText()
		val listType = object : TypeToken<List<PunkItem>>() {}.type
		val list: List<PunkItem> = Gson().fromJson(text, listType)
		return list
	}

	data class PunkItem(
			val id: Int, //192
			val name: String, //Punk IPA 2007 - 2010
			val tagline: String, //Post Modern Classic. Spiky. Tropical. Hoppy.
			val first_brewed: String, //04/2007
			val description: String, //Our flagship beer that kick started the craft beer revolution. This is James and Martin's original take on an American IPA, subverted with punchy New Zealand hops. Layered with new world hops to create an all-out riot of grapefruit, pineapple and lychee before a spiky, mouth-puckering bitter finish.
			val image_url: String, //https://images.punkapi.com/v2/192.png
			val abv: Float, //6.0
			val ibu: Float, //60.0
			val target_fg: Float, //1010.0
			val target_og: Float, //1056.0
			val ebc: Float, //17.0
			val srm: Float, //8.5
			val ph: Float, //4.4
			val attenuation_level: Float, //82.14
			val volume: Volume,
			val boil_volume: BoilVolume,
			val method: Method,
			val ingredients: Ingredients,
			val food_pairing: List<String>,
			val brewers_tips: String, //While it may surprise you, this version of Punk IPA isn't dry hopped but still packs a punch! To make the best of the aroma hops make sure they are fully submerged and add them just before knock out for an intense hop hit.
			val contributed_by: String //Sam Mason <samjbmason>
	)

	data class Ingredients(
			val malt: List<Malt>,
			val hops: List<Hop>,
			val yeast: String //Wyeast 1056 - American Ale™
	)

	data class Hop(
			val name: String, //Ahtanum
			val amount: Amount,
			val add: String, //start
			val attribute: String //bitter
	)

	data class Amount(
			val value: Float, //17.5
			val unit: String //grams
	)

	data class Malt(
			val name: String, //Extra Pale
			val amount: Amount
	)

	data class Volume(
			val value: Int, //20
			val unit: String //liters
	)

	data class BoilVolume(
			val value: Int, //25
			val unit: String //liters
	)

	data class Method(
			val mash_temp: List<MashTemp>,
			val fermentation: Fermentation,
			val twist: Any //null
	)

	data class Fermentation(
			val temp: Temp
	)

	data class MashTemp(
			val temp: Temp,
			val duration: Int //75
	)

	data class Temp(
			val value: Int, //65
			val unit: String //celsius
	)
}
