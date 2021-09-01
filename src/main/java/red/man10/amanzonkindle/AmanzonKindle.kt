package red.man10.amanzonkindle

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import red.man10.man10bank.BankAPI
import red.man10.man10bank.Man10Bank
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.sql.ResultSet
import java.text.SimpleDateFormat

class AmanzonKindle: JavaPlugin() {

    val prefix = "§f§l[§e§lKindle§f§l]"

    var catelist = mutableListOf<String>()

    var mysql: MySQLManager? = null

    var v: VaultManager? = null

    val pagemap = HashMap<Player, MutableList<ItemStack>>()

    var bank: BankAPI? = null

    val gui = GUIProcess(this)
    val util = Utility()

    override fun onEnable() {
        saveDefaultConfig()

        mysql = MySQLManager(this, "Kindle")

        v = VaultManager(this)

        getCommand("amk")!!.setExecutor(Commands(this))
        server.pluginManager.registerEvents(Events(this), this)

        bank = BankAPI(this)

        if (config.contains("category")) catelist = config.getList("category") as MutableList<String>

    }

    override fun onDisable() {

    }



}