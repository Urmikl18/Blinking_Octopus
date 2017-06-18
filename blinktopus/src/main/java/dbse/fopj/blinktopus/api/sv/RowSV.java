package dbse.fopj.blinktopus.api.sv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonProperty;

import dbse.fopj.blinktopus.api.datamodel.LineItem;
import dbse.fopj.blinktopus.api.datamodel.Order;
import dbse.fopj.blinktopus.api.datamodel.Tuple;
import dbse.fopj.blinktopus.api.managers.LogManager;
import dbse.fopj.blinktopus.api.resultmodel.LogResult;
import dbse.fopj.blinktopus.api.resultmodel.Result;
import dbse.fopj.blinktopus.api.resultmodel.SVResult;
import dbse.fopj.blinktopus.resources.QueryProcessor;

/**
 * 
 * @author urmikl18 Class represents row oriented db.\n List of
 *         {@link RowEntry}s: id: tuple : position in log
 */
public class RowSV extends SV {
	public class RowEntry {
		private int id;
		private Tuple value;
		private int pos;

		public RowEntry() {
		}

		public RowEntry(int id, Tuple value, int pos) {
			this.id = id;
			this.value = value;
			this.pos = pos;
		}

		@JsonProperty
		public int getId() {
			return id;
		}

		@JsonProperty
		public Tuple getValue() {
			return value;
		}

		@JsonProperty
		public int getPosition() {
			return pos;
		}
	}

	private List<RowEntry> rowData;

	public RowSV() {
	}

	public RowSV(String id, String table, String attr, double lower, double higher) {
		super(id, "Row", table, attr, lower, higher);
		List<Tuple> all = LogManager.getLogManager().getAllLog().getResultTuples();
		LogResult res = LogManager.getLogManager().scan(table, attr, lower, higher, "");
		List<Tuple> lr = res.getResultTuples();
		this.setTime(res.getTimeLog());
		this.rowData = IntStream.range(0, lr.size())
				.mapToObj(i -> new RowEntry(i + 1, lr.get(i), all.indexOf(lr.get(i)))).collect(Collectors.toList());
		this.setSize(rowData.size());
	}

	private RowSV(String id, String table, String attr, double lower, double higher, List<RowEntry> rowData) {
		super(id, "Row", table, attr, lower, higher);
		this.rowData = rowData;
		this.setSize(rowData.size());
	}

	@JsonProperty
	public List<RowEntry> getRowData() {
		return rowData;
	}

	/**
	 * 
	 * @param table
	 *            - name of a relation to query on
	 * @param attr
	 *            - name of an attribute to query on
	 * @param lower
	 *            - left border of an interval
	 * @param higher
	 *            - right border of an interval
	 * @return SVResult with relevant information Works as follows: 1. Check if
	 *         table name and attribute name are of this SV, check if range is a
	 *         subrange of current one:\n 1.1 YES - create temporary RowSV and
	 *         return it as a result. 1.2 NO - create new RowSV and store it in
	 *         SVManager
	 */
	public Result query(String table, String attr, double lower, double higher) {
		long start = 0;
		long timeSV = 0;
		if (this.getTable().toLowerCase().equals(table.toLowerCase())
				&& this.getAttr().toLowerCase().equals(attr.toLowerCase()) && this.getLower() <= lower
				&& this.getHigher() >= higher) {
			List<RowEntry> res = new ArrayList<RowEntry>();
			if (table.toLowerCase().equals("orders")) {
				switch (QueryProcessor.attrIndex.get(attr.toLowerCase())) {
				case 0:
					start = System.nanoTime();
					res = this.rowData.stream().filter((RowEntry e) -> ((Order) e.getValue()).getTotalPrice() >= lower
							&& ((Order) e.getValue()).getTotalPrice() <= higher).collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 1:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((Order) e.getValue()).getOrderDate().getTime() >= lower
									&& ((Order) e.getValue()).getOrderDate().getTime() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				default:
					res = null;
					break;
				}
			} else {
				switch (QueryProcessor.attrIndex.get(attr.toLowerCase())) {
				case 0:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((LineItem) e.getValue()).getLineNumber() >= lower
									&& ((LineItem) e.getValue()).getLineNumber() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 1:
					start = System.nanoTime();
					res = this.rowData.stream().filter((RowEntry e) -> ((LineItem) e.getValue()).getQuantity() >= lower
							&& ((LineItem) e.getValue()).getQuantity() <= higher).collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 2:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((LineItem) e.getValue()).getExtendedPrice() >= lower
									&& ((LineItem) e.getValue()).getExtendedPrice() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 3:
					start = System.nanoTime();
					res = this.rowData.stream().filter((RowEntry e) -> ((LineItem) e.getValue()).getDiscount() >= lower
							&& ((LineItem) e.getValue()).getDiscount() <= higher).collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 4:
					start = System.nanoTime();
					res = this.rowData.stream().filter((RowEntry e) -> ((LineItem) e.getValue()).getTax() >= lower
							&& ((LineItem) e.getValue()).getTax() <= higher).collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 5:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((LineItem) e.getValue()).getShipDate().getTime() >= lower
									&& ((LineItem) e.getValue()).getShipDate().getTime() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 6:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((LineItem) e.getValue()).getCommitDate().getTime() >= lower
									&& ((LineItem) e.getValue()).getCommitDate().getTime() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				case 7:
					start = System.nanoTime();
					res = this.rowData.stream()
							.filter((RowEntry e) -> ((LineItem) e.getValue()).getReceiptDate().getTime() >= lower
									&& ((LineItem) e.getValue()).getReceiptDate().getTime() <= higher)
							.collect(Collectors.toList());
					timeSV = System.nanoTime() - start;
					break;
				default:
					res = null;
					break;
				}
			}
			long timeLog = LogManager.getLogManager().getTime(table, attr, lower, higher, "");
			return new SVResult(this.getId() + "tmp", "Row", table, attr, lower, higher, timeLog, timeSV, res.size(), 0,
					0, "OK", new RowSV(this.getId() + "tmp", table, attr, lower, higher, res));

		} else {
			if (!this.getTable().toLowerCase().equals(table.toLowerCase()))
				return LogManager.getLogManager().scan(table, attr, lower, higher,
						"SV with Id: " + this.getId() + " is not for the table: " + table);
			else if (!this.getAttr().toLowerCase().equals(attr.toLowerCase()))
				return LogManager.getLogManager().scan(table, attr, lower, higher,
						"SV with Id: " + this.getId() + " is not for attribute: " + attr);
			else
				return LogManager.getLogManager().scan(table, attr, lower, higher, "Random error");
		}
	}

	public double getCount(String table, String attr, double lower, double higher, boolean distinct, String message) {
		if (this.getTable().toLowerCase().equals(table.toLowerCase())
				&& this.getAttr().toLowerCase().equals(attr.toLowerCase()) && this.getLower() <= lower
				&& this.getHigher() >= higher) {
			if (!distinct) {
				return this.getSize();
			} else {
				HashMap<Double, Tuple> hm = new HashMap<>();
				for (RowEntry v : this.rowData) {
					Tuple t =v.getValue();
					if (table.toLowerCase().equals("orders")) {
						switch (QueryProcessor.attrIndex.get(attr.toLowerCase())) {
						case 0:
							hm.put(((Order) t).getTotalPrice(), t);
							break;
						case 1:
							hm.put((double) ((Order) t).getOrderDate().getTime(), t);
							break;
						default:
							break;
						}
					} else {
						switch (QueryProcessor.attrIndex.get(attr.toLowerCase())) {
						case 0:
							hm.put((double) ((LineItem) t).getLineNumber(), t);
							break;
						case 1:
							hm.put((double) ((LineItem) t).getQuantity(), t);
							break;
						case 2:
							hm.put((double) ((LineItem) t).getExtendedPrice(), t);
							break;
						case 3:
							hm.put((double) ((LineItem) t).getDiscount(), t);
							break;
						case 4:
							hm.put((double) ((LineItem) t).getTax(), t);
							break;
						case 5:
							hm.put((double) ((LineItem) t).getShipDate().getTime(), t);
							break;
						case 6:
							hm.put((double) ((LineItem) t).getCommitDate().getTime(), t);
							break;
						case 7:
							hm.put((double) ((LineItem) t).getReceiptDate().getTime(), t);
							break;
						default:
							break;
						}
					}
				}
				return hm.size();
			}
		}
		else
			return LogManager.getLogManager().getCount(table, attr, lower, higher, distinct, message);
	}

}
