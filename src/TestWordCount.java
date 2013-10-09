import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.MapDriver;
import org.apache.hadoop.mrunit.MapReduceDriver;
import org.apache.hadoop.mrunit.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class TestWordCount {
	
	Log logging = LogFactory.getLog(getClass());
	
	MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable> mapReduceDriver;
	MapDriver<LongWritable, Text, Text, IntWritable> mapDriver;
	ReduceDriver<Text, IntWritable, Text, IntWritable> reduceDriver;

	@Before
	public void setUp() {
		WordMapper mapper = new WordMapper();
		SumReducer reducer = new SumReducer();
		mapDriver = new MapDriver<LongWritable, Text, Text, IntWritable>();
		mapDriver.setMapper(mapper);
		reduceDriver = new ReduceDriver<Text, IntWritable, Text, IntWritable>();
		reduceDriver.setReducer(reducer);
		mapReduceDriver = new MapReduceDriver<LongWritable, Text, Text, IntWritable, Text, IntWritable>();
		mapReduceDriver.setMapper(mapper);
		mapReduceDriver.setReducer(reducer);
	}

	@Test
	public void testMapper() {
		logging.info("testMapper()");
		mapDriver.withInput(new LongWritable(1), new Text("cat cat dog"));
		mapDriver.withOutput(new Text("cat"), new IntWritable(1));
		mapDriver.withOutput(new Text("cat"), new IntWritable(1));
		mapDriver.withOutput(new Text("dog"), new IntWritable(1));
		mapDriver.runTest();
	}

	@Test
	public void testReducer() {
		logging.info("testReducer()");
		List<IntWritable> values = new ArrayList<IntWritable>();
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		values.add(new IntWritable(1));
		reduceDriver.withInput(new Text("cat"), values);
		reduceDriver.withOutput(new Text("cat"), new IntWritable(3));
		reduceDriver.runTest();
		
		reduceDriver.resetOutput();
		reduceDriver.withInput(new Text("dog"), Collections.singletonList(new IntWritable(2)));
		reduceDriver.withOutput(new Text("dog"), new IntWritable(2));
		reduceDriver.runTest();
	}

	@Test
	public void testMapReduce() {
		logging.info("testMapReduce()");
		mapReduceDriver.withInput(new LongWritable(1), new Text("cat cat dog"));
		mapReduceDriver.addOutput(new Text("cat"), new IntWritable(2));
		mapReduceDriver.addOutput(new Text("dog"), new IntWritable(1));
		mapReduceDriver.runTest();
	}

}
