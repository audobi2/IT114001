
public class JavaLoops {
	public static void main(String[] args) {
		int[] numArr = new int[] {8, 10, 10, 13, 17, 20, 95, 96, 97, 98, 99};
		
		//for each loop that loops through each int in the array, no matter how long the array is
		for(int num : numArr) {
			//checks if even by seeing if remainder is 0 - if so, print the number
			if(num % 2 == 0) {
				System.out.println(num);
			}
		}
	}
}
