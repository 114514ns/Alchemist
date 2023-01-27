package cn.pprocket.alchemist

class StringUtil {
    fun getChestUrl(chestName:String) {
        var s = "https://buff.163.com/api/market/csgo_container?container=${chestName}&is_container=1&container_type=itemset&unusual_only=0&game=csgo&appid=730"
    }
}