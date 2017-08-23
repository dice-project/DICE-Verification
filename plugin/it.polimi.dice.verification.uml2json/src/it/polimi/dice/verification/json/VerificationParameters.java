package it.polimi.dice.verification.json;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

import it.polimi.dice.vtconfig.ZotPlugin;

public class VerificationParameters {
	
		// TODO: cleanup code
		@SerializedName(value="plugin")
		private List<String> plugins;
		@SerializedName(value="max_time")
		private int maxTime;
		// TODO: convert nume_steps to time_bound in the server
		@SerializedName(value="time_bound")
		private int timeBound;
	
/*		@SerializedName(value="base_quantity")
		private int baseQuantity;
		@SerializedName(value="periodic_queues")
		private List<String> periodicQueues;
		@SerializedName(value="strictly_monotonic_queues")
		private List<String> strictlyMonotonicQueues;
*/
	
		public VerificationParameters(){
			this.plugins = new ArrayList<>();
			plugins.add(ZotPlugin.AE2SBVZOT.getName());
			this.maxTime = 2000;
			this.timeBound = 15;
/*			this.baseQuantity = 5;
			this.periodicQueues = new ArrayList<>();
			this.strictlyMonotonicQueues = new ArrayList<>();			
*/		}

		public List<String> getPlugins() {
			return plugins;
		}

		public void setPlugins(List<String> plugins) {
			this.plugins = plugins;
		}

		public int getMaxTime() {
			return maxTime;
		}

		public void setMaxTime(int maxTime) {
			this.maxTime = maxTime;
		}

/*		public int getBaseQuantity() {
			return baseQuantity;
		}

		public void setBaseQuantity(int baseQuantity) {
			this.baseQuantity = baseQuantity;
		}
*/
		public int getTimeBound() {
			return timeBound;
		}

		public void setTimeBound(int timeBound) {
			this.timeBound = timeBound;
		}

/*		public List<String> getPeriodicQueues() {
			return periodicQueues;
		}

		public void setPeriodicQueues(List<String> periodicQueues) {
			this.periodicQueues = periodicQueues;
		}
		
		public void setPeriodicQueuesFromBolts(List<BoltClass> periodicQueues) {
			List<String> result = new ArrayList<>();
			for (BoltClass bolt : periodicQueues) {
				result.add(bolt.getId());
			}
			this.periodicQueues = result;
		}

		public List<String> getStrictlyMonotonicQueues() {
			return strictlyMonotonicQueues;
		}

		public void setStrictlyMonotonicQueues(List<String> strictlyMonotonicQueues) {
			this.strictlyMonotonicQueues = strictlyMonotonicQueues;
		}*/
}

