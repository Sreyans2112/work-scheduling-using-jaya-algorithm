

package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Cloudlet2;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.Vm3;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.lists.VmList;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


/**
 * A simple example showing how to create
 * two datacenters with one host each and
 * run two cloudlets on them.
 */
public class Jaya{

	/** The cloudlet list. */
	private static List<Cloudlet2> cloudletList;

	/** The vmlist. */
	private static List<Vm3> vmlist;
	public static int n=10;
	public static int p=3;
	public static int alloc[][] = new int[n][n];
	public static double alloc_cost[] = new double[n];
	public static int alloc2[][] = new int[n][n];
	public static double alloc_cost2[] = new double[n];
	public static int x[] = new int[n];
	public static double task_cost[][] = new double[n][p];
	public static int i,j;
	public static int merge[][] = new int[2*n][n];
	public static double merge_cost[] = new double[2*n] ;

	public static int best[] = new int[n];
	public static int worst[] = new int[n];
	public static double best_cost=99999999999999999.0;
	public static double worst_cost;
	public static double TP[][] = new double[n][p];
	public static double PP[][] = new double[p][p];
	public static double data[][] = new double[n][2];


	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {

		Log.printLine("Starting Jaya...");

		try {
			// First step: Initialize the CloudSim package. It should be called
			// before creating any entities.
			int num_user = 1;   // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false;  // mean trace events

			// Initialize the GridSim library
			CloudSim.init(num_user, calendar, trace_flag);

			// Second step: Create Datacenters
			//Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
			@SuppressWarnings("unused")
			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			@SuppressWarnings("unused")
			//Datacenter datacenter1 = createDatacenter("Datacenter_1");

			//Third step: Create Broker
			DatacenterBroker broker = createBroker();
			int brokerId = broker.getId();

			//Fourth step: Create one virtual machine
			vmlist = new ArrayList<Vm3>();

			//VM description
			int vmid = 0;
			int mips = 250;
			long size = 10000; //image size (MB)
			int ram = 512; //vm memory (MB)
			long bw = 1000;
			int pesNumber = 1; //number of cpus
			String vmm = "Xen"; //VMM name
			double procost[] = new double[3];
			//create two VMs
			double PP1[][] = {{0.00, 0.17, 0.21},	//PC x PC Communiction cost
					 {0.17, 0.00, 0.22},
					 {0.21, 0.22, 0.00}};
			for(int i=0;i<p;i++)
			{
				procost = PP1[i].clone();
				Vm3 vm1 = new Vm3(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared(), procost);
				vmid++;
				vmlist.add(vm1);
			}

			//submit vm list to the broker
			broker.submitVmList(vmlist);


			//Fifth step: Create two Cloudlets
			cloudletList = new ArrayList<Cloudlet2>();

			//Cloudlet properties
			int id = 0;
			long length = 40000;
			long fileSize = 300;
			long outputSize = 300;
			UtilizationModel utilizationModel = new UtilizationModelFull();
			double taskaccess[] = new double[3];
			double taskdata[] = new double[2];
			double TP1[][] = {{1.23 ,1.12, 1.25},  //Task x PC
                    {1.27 ,0.17, 1.28},
                    {0.13 ,1.11, 2.11},
                    {1.26 ,1.12, 0.14},
                    {1.89 ,1.14, 1.22},
                    {1.27 ,0.47, 1.28},
                    {0.13 ,1.11, 1.11},
                    {1.26 ,1.62, 0.14},
                    {1.13 ,1.12, 1.25},
                    {1.89 ,1.14, 0.42}};
			double data1[][] = {{30,30}, {10,10},{10,10},{10,10},{30,60},{30,50},{30,20},{70,60},{30,40},{30,60}};
			for(int i=0;i<10;i++)
			{
				taskaccess = TP1[i].clone();
				taskdata = data1[i].clone();
				Cloudlet2 cloudlet1 = new Cloudlet2(id, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel,taskaccess,taskdata);
				cloudlet1.setUserId(brokerId);
				//add the cloudlets to the list
				cloudletList.add(cloudlet1);
				id++;

			}
			for(i=0;i<n;i++)
			{
				TP[i] = cloudletList.get(i).accesscost;
				data[i] = cloudletList.get(i).datasize;
			}
			
			
			for(i=0;i<p;i++)
			{
				PP[i] = vmlist.get(i).commcost;
			}
			
			for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
				{
					Random randomno = new Random();
					alloc[i][j] = randomno.nextInt(3);
				}
			}

			for(i=0;i<n;i++)
			{
				for(j=0;j<n;j++)
					x[j] = alloc[i][j];

				task_cost = calculate_costs(TP,PP,data,x);
				alloc_cost[i] = fitness(x,task_cost);
				//System.out.println(alloc_cost[i]);

			}

			int min_idx;
			double swap_cost;
			int swap_item;
			int pp;
			// One by one move boundary of unsorted subarray
			//sorting on alloc
	    	for (i = 0; i < n-1; i++)
	    	{
	        	// Find the minimum element in unsorted array
	        	min_idx = i;
	        	for (j = i+1; j < n; j++)
	          		if (alloc_cost[j] < alloc_cost[min_idx])
	            		min_idx = j;
	 
	        	// Swap the found minimum element with the first element
	        	swap_cost = alloc_cost[i];
	        	alloc_cost[i] = alloc_cost[min_idx];
	        	alloc_cost[min_idx] = swap_cost;
	        	for(pp=0;pp<n;pp++)
	        	{
	        		swap_item = alloc[i][pp];
	        		alloc[i][pp] = alloc[min_idx][pp];
	        		alloc[min_idx][pp] = swap_item;
	        	}

	    	}

			int iter;
			long timestart = System.currentTimeMillis();
			for(iter=0;iter<10000;iter++)
			{
				allocated_processor();
				//for(i=0;i<n;i++)
				//	System.out.print(best[i]);
				//System.out.println(best_cost);
				
			}
			long timeend = System.currentTimeMillis();
			for(i=0;i<n;i++)
				System.out.print(best[i]+" ");
			System.out.println(best_cost);
			System.out.println(timeend-timestart);


						
		

			//submit cloudlet list to the broker
			broker.submitCloudletList(cloudletList);
			
			//creation of TP,PP,data from cloudlets and vmlist
			
			
			int i;
			for(i=0;i<n;i++)
			{
				TP[i] = cloudletList.get(i).accesscost;
				data[i] = cloudletList.get(i).datasize;
			}
			
			
			for(i=0;i<p;i++)
			{
				PP[i] = vmlist.get(i).commcost;
			}
			
			
			//long timestart = System.currentTimeMillis();
			//int iter;
			
			
			System.out.println(timeend-timestart);


			long timeend1 = System.currentTimeMillis();
			
			//BINDING OF FINAL MAPPING
			
			for(i=0;i<n;i++)
			{
				broker.bindCloudletToVm(i,best[i]);
			}
			
			//System.out.println(g_fitness1);
			System.out.println(timeend1-timestart);

			//bind the cloudlets to the vms. This way, the broker
			// will submit the bound cloudlets only to the specific VM
			//broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
			
			// Sixth step: Starts the simulation
			CloudSim.startSimulation();


			// Final step: Print results when simulation is over
			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

        	printCloudletList(newList);

			Log.printLine("HoneyBee finished!");
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}

	private static Datacenter createDatacenter(String name){

		// Here are the steps needed to create a PowerDatacenter:
		// 1. We need to create a list to store
		//    our machine
		List<Host> hostList = new ArrayList<Host>();

		// 2. A Machine contains one or more PEs or CPUs/Cores.
		// In this example, it will have only one core.
		List<Pe> peList = new ArrayList<Pe>();

		int mips = 10000;

		// 3. Create PEs and add these into a list.
		peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
		peList.add(new Pe(1, new PeProvisionerSimple(mips)));
		
		//4. Create Host with its id and list of PEs and add them to the list of machines
		int hostId=0;
		int ram = 2048*4; //host memory (MB)
		long storage = 1000000; //host storage
		int bw = 10000;


		//in this example, the VMAllocatonPolicy in use is SpaceShared. It means that only one VM
		//is allowed to run on each Pe. As each Host has only one Pe, only one VM can run on each Host.
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		); // This is our first machine
		hostId++;
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram),
    				new BwProvisionerSimple(bw),
    				storage,
    				peList,
    				new VmSchedulerSpaceShared(peList)
    			)
    		);
		// 5. Create a DatacenterCharacteristics object that stores the
		//    properties of a data center: architecture, OS, list of
		//    Machines, allocation policy: time- or space-shared, time zone
		//    and its price (G$/Pe time unit).
		String arch = "x86";      // system architecture
		String os = "Linux";          // operating system
		String vmm = "Xen";
		double time_zone = 10.0;         // time zone this resource located
		double cost = 3.0;              // the cost of using processing in this resource
		double costPerMem = 0.05;		// the cost of using memory in this resource
		double costPerStorage = 0.001;	// the cost of using storage in this resource
		double costPerBw = 0.0;			// the cost of using bw in this resource
		LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

	       DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
	                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);


		// 6. Finally, we need to create a PowerDatacenter object.
		Datacenter datacenter = null;
		try {
			datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	//We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
	//to the specific rules of the simulated scenario
	private static DatacenterBroker createBroker(){

		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Prints the Cloudlet objects
	 * @param list  list of Cloudlets
	 */
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent +
				"Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS){
				Log.print("SUCCESS");

				Log.printLine( indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId() +
						indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent + dft.format(cloudlet.getExecStartTime())+
						indent + indent + dft.format(cloudlet.getFinishTime()));
			}
		}

	}
	
	
	public static void allocated_processor()
	{
		

    	//storing best

    	for(i=0;i<n;i++)
    		best[i] = alloc[0][i];
    	best_cost = alloc_cost[0];
    	//System.out.println(best_cost);
    	//storing worst

    	for(i=0;i<n;i++)
    		worst[i] = alloc[n-1][i];
    	worst_cost = alloc_cost[n-1];

    	Random r = new Random();
    	//using equation  

    	for(i=0;i<n;i++)
    	{
    		for(j=0;j<n;j++)
    		{
    			alloc2[i][j] = (alloc[i][j] + Math.abs((int)((r.nextDouble())*(best[j]-alloc[i][j]) - (r.nextDouble())*(worst[j]-alloc[i][j]))))%3;
    		}
    	}

    	/*for(i=0;i<n;i++)
    	{
    		for(j=0;j<n;j++)
    		{
    			System.out.print(alloc2[i][j]);
    		}
    		System.out.println();
    	}*/

    	for(i=0;i<n;i++)
		{
			for(j=0;j<n;j++)
				x[j] = alloc2[i][j];

			task_cost = calculate_costs(TP,PP,data,x);
			alloc_cost2[i] = fitness(x,task_cost);

		}


		int min_idx;
		double swap_cost;
		int swap_item;
		int pp;
		//sorting on alloc2
    	for (i = 0; i < n-1; i++)
    	{
        	// Find the minimum element in unsorted array
        	min_idx = i;
        	for (j = i+1; j < n; j++)
          		if (alloc_cost2[j] < alloc_cost2[min_idx])
            		min_idx = j;
 
        	// Swap the found minimum element with the first element
        	swap_cost = alloc_cost2[i];
        	alloc_cost2[i] = alloc_cost2[min_idx];
        	alloc_cost2[min_idx] = swap_cost;
        	for(pp=0;pp<n;pp++)
        	{
        		swap_item = alloc2[i][pp];
        		alloc2[i][pp] = alloc2[min_idx][pp];
        		alloc2[min_idx][pp] = swap_item;
        	}

    	}

    	//merging alloc and alloc2 based on costs

    	i=0;
    	j=0;
    	int k=0;
    	
    	while(i<n && j<n)
    	{
    		if(alloc_cost[i]<alloc_cost2[j])
    		{
    			merge_cost[k]=alloc_cost[i];

    			for(pp=0;pp<n;pp++)
    				merge[k][pp] = alloc[i][pp];

    			i++;
    			k++;

    		}
    		else if(alloc_cost[i]>=alloc_cost2[j])
    		{
    			merge_cost[k]=alloc_cost2[j];

    			for(pp=0;pp<n;pp++)
    				merge[k][pp] = alloc2[j][pp];

    			j++;
    			k++;

    		}
    	}

    	for(i=0;i<n;i++)  //sending top n allocations to alloc
    	{
    		for(j=0;j<n;j++)
    		{
    			alloc[i][j] = merge[i][j];
    		}
    		alloc_cost[i] = merge_cost[i];
    	}

	}

	public static double fitness(int x[], double task_cost[][])
	{
		double total_cost = 0.0;
		int i=0;
		for(i=0;i<n;i++)
		{
			//System.out.println("x[i] : "+x[i]);
			total_cost+=task_cost[i][x[i]];
		}
		return total_cost;
	}

	public static double[][] calculate_costs(double TP[][], double PP[][], double data[][],int x[])
	{
		//sum of every task on each PC - exe cost of a PC
		
		double exec[] = new double[p];
		
		int i,j;
		double sum=0.0;
		for(i=0;i<n;i++)	//execution cost Cexe
		{
			
			exec[x[i]]+=TP[i][x[i]];
		}
		//for(i=0;i<3;i++)
		//	System.out.println("Exec cost: "+exec[i]);

		//Communication cost

		double comm_cost[] = new double[p];

		for(i=0;i<p;i++)
		{
			sum=0.0;
			for(j=0;j<p;j++)
			{
				sum+=PP[i][j];
			}
			comm_cost[i] = sum;
		}

		//Cost of every task on
		double task_cost[][] = new double[n][p];
		for(i=0;i<n;i++)
		{
			for(j=0;j<p;j++)
			{
				task_cost[i][j] = data[i][0]*comm_cost[j] + exec[j];
				//System.out.println(task_cost[i][j]);
			}
		}
		return task_cost;
	}
}