package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

public class VolumeConfig {

	class Range {
		private String max;
		private String min;

		public Range() {
			this.max = "1000";
			this.min = "10";
		}

		public Range(String max, String min) {
			this.max = max;
			this.min = min;
		}

		public String getMax() {
			return max;
		}

		public void setMax(String max) {
			this.max = max;
		}

		public String getMin() {
			return min;
		}

		public void setMin(String min) {
			this.min = min;
		}

	}

	private List<VolumeType> type;
	private Range range;

	public VolumeConfig() {
		this.type = new ArrayList<VolumeType>();
		this.range = new Range();
	}

	public List<VolumeType> getType() {
		return type;
	}

	public void setType(List<VolumeType> type) {
		this.type = type;
	}

	public void addType(VolumeType volumeType) {
		this.type.add(volumeType);
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public void makeRange(String max, String min) {
		this.range = new Range(max, min);
	}

}
