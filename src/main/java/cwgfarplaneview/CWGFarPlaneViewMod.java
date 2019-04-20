package cwgfarplaneview;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import cwgfarplaneview.command.CWGFarPlaneViewCommand;
import cwgfarplaneview.event.CWGFarPlaneViewEventHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = CWGFarPlaneViewMod.MODID, name = CWGFarPlaneViewMod.NAME, version = CWGFarPlaneViewMod.VERSION, dependencies = CWGFarPlaneViewMod.DEPENCIES)
public class CWGFarPlaneViewMod {
	public static final String MODID = "cwgfarplaneview";
	public static final String NAME = "CWG Far plane view";
	public static final String VERSION = "0.1.2";
	public static final String DEPENCIES = "required:cubicchunks@[0.0.938.0,);required:cubicgen@[0.0.39.0,);required:forge@[14.23.3.2658,)";
	
	@SidedProxy(clientSide = "cwgfarplaneview.ClientProxy", serverSide = "cwgfarplaneview.ServerProxy")
	public static ServerProxy proxy;
	
	@SidedProxy(clientSide = "cwgfarplaneview.ClientNetworkHandler", serverSide = "cwgfarplaneview.ServerNetworkHandler")
	public static ServerNetworkHandler network;
	
	
	public static Logger logger;
	public static CWGFarPlaneViewEventHandler eventHandler;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.preInit();
		network.load();
		logger = event.getModLog();
		eventHandler = new CWGFarPlaneViewEventHandler();
		MinecraftForge.EVENT_BUS.register(eventHandler);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
	}

	@EventHandler
	public void serverStart(FMLServerStartingEvent event) {
		network.setServer(event.getServer());
		event.registerServerCommand(new CWGFarPlaneViewCommand());
	}
}