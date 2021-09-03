package red.man10.amanzonkindle

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import red.man10.man10bank.BankAPI

class AmanzonKindle: JavaPlugin() {

    val prefix = "§f§l[§e§lKindle§f§l]"

    var catelist = mutableListOf<String>()

    var mysql: MySQLManager? = null

    var v: VaultManager? = null

    val pagemap = HashMap<Player, MutableList<ItemStack>>()

    var bank: BankAPI? = null

    val gui = GUIProcess(this)
    val util = Utility()

    var cost = 10000.0

    override fun onEnable() {
        saveDefaultConfig()

        mysql = MySQLManager(this, "Kindle")

        v = VaultManager(this)

        getCommand("amk")!!.setExecutor(Commands(this))
        server.pluginManager.registerEvents(Events(this), this)

        bank = BankAPI(this)

        loadConfig()

    }

    override fun onDisable() {

    }

    fun loadConfig(){

        reloadConfig()

        if (config.contains("category")) catelist = config.getList("category") as MutableList<String>

        if (config.contains("publish_cost")) cost = config.getDouble("publish_cost")
    }

}