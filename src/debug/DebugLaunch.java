package debug;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import graphing.Panel;
import optimization.algorithm.PSOAlgorithm;
import optimization.benchmarks.OpFunction;
import util.Point;

public class DebugLaunch{
	
	public static void main(String[] args){
//		OpFunction f = new OpFunction.InvertFunction(new OpFunction.Rosenbrock().setA(5));
//		PSOAlgorithm pso = new PSOAlgorithm().setMaxNumberOfIterations(10000);
//		Point best = pso.optimize(f);
//		System.out.println(best+" "+f.value(best));
		test();
	}
	
	private static void test(){
		Test t = new Test(0);
		long start = System.currentTimeMillis();
		for(int c=0; c<100000; c++){
			//while(t.getVal()<9999){
				//t.setVal(t.getVal()+1); //348
				//test2(t); //340
			//}
			test3(t);//2759
			t.setVal(0);
		}
		System.out.println(System.currentTimeMillis()-start);
	}
	
	private static void test2(Test t){
		t.setVal(t.getVal()+1);
	}
	
	private static void test3(Test t){
		if(t.getVal()==9999){
			return;
		}
		else{
			t.setVal(t.getVal()+1);
			test3(t);
		}
	}
	
	
	@AllArgsConstructor private static class Test{
		@Getter @Setter long val;
	}

}
