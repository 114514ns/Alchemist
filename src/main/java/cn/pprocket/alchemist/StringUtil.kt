package cn.pprocket.alchemist

import cn.hutool.core.util.URLUtil
import cn.pprocket.alchemist.internal.ChestType

class StringUtil {

    fun getChestUrl(chestName:String,type:ChestType):String {
        var str =if (type == ChestType.MAP_COLLECTION) {
            "itemset"
        } else {
            "weaponcase"
        }
        var value =  "https://buff.163.com/api/market/csgo_container?container=${encodeUrl(chestName)}&is_container=1&container_type=${str}&unusual_only=0&game=csgo&appid=730"
        println(value)
        return value
    }
    fun encodeUrl(url:String):String {
        var result = url.replace("&","%26")
        result = result.replace(" ","%20")
        return result
    }
}