public class Main {

    public static void main(String[] args) {
        // initial data
        int[] supply = {160, 140, 170};
        int[][] costs = {
            {7, 8, 1, 2},
            {4, 5, 9, 8},
            {9, 2, 3, 6},
        };
        int[] demand = {120, 50, 190, 110};

        //check the applickability (if all values are > 0)
        for (int i = 0; i < supply.length; i++) {
            if (supply[i] < 0) {
                System.out.println("The method is not applicable!");
                System.exit(0);
            }
            for (int j = 0; j < demand.length; j++) {
                if (costs[i][j] < 0) {
                    System.out.println("The method is not applicable!");
                    System.exit(0);
                }
            }
        }
        for (int i = 0; i < demand.length; i++) {
            if (demand[i] < 0) {
                System.out.println("The method is not applicable!");
                System.exit(0);
            }
        }

        //check the balancing of supply and demand
        int sumSupply = 0;
        for (int i = 0; i < supply.length; i++) {
            sumSupply += supply[i];
        }
        int sumDemand = 0;  
        for (int i = 0; i < demand.length; i++) {
            sumDemand += demand[i];
        }
        if (sumSupply != sumDemand) {
            System.out.println("The problem is not balanced!");
            System.exit(0);
        }

        //creating an optimal matrix for solving the problem
        int[][] matrix = createMatrix(supply, costs, demand);

        //printing the table
        int counter = 1;
        System.out.printf("+--------+--------+--------+--------+--------+--------+ \n");
        System.out.printf("|%7s |%7s |%7s |%7s |%7s |%7s | \n", " ", "D1", "D2", "D3", "D4", "Supply");
        System.out.printf("+--------+--------+--------+--------+--------+--------+ \n");
        for (int i = 0; i < supply.length; i++) {
            System.out.printf("|%7s ", "S" + counter++);
            for (int j = 0; j < demand.length + 1; j++) {
                System.out.printf("|%7d ", matrix[i][j]);
            }
            System.out.printf("| \n");
            System.out.printf("+--------+--------+--------+--------+--------+--------+ \n");
        }
        System.out.printf("|%7s ", "Demand");
        for (int i = 0; i < demand.length; i++) {
            System.out.printf("|%7d ", matrix[supply.length][i]);
        }
        System.out.printf("|%7s |\n", "");
        System.out.printf("+--------+--------+--------+--------+--------+--------+ \n");

        //North-West corner method
        int [][] matrixNW = createMatrix(supply, costs, demand);
        int[][] feasibleSols = new int[supply.length][demand.length];
        int answer = 0;
        int curSupply = 0;
        int curDemand = 0;
        int minVariant = 0;
        while (true) {
            //select the minimum variant of supply/demand
            if(matrixNW[curSupply][demand.length] > matrixNW[supply.length][curDemand]) {
                minVariant = matrixNW[supply.length][curDemand];
                //update supply/demand
                matrixNW[curSupply][demand.length] -= minVariant;
                matrixNW[supply.length][curDemand] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[curSupply][curDemand] = minVariant;
                answer += minVariant * costs[curSupply][curDemand];
                //move right
                curDemand++;
            } else {
                minVariant = matrixNW[curSupply][demand.length];
                //update supply/demand
                matrixNW[curSupply][demand.length] -= minVariant;
                matrixNW[supply.length][curDemand] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[curSupply][curDemand] = minVariant;
                answer += minVariant * costs[curSupply][curDemand];
                //move down
                curSupply++;
            }
            //check that we can finish
            if (curSupply == supply.length || curDemand == demand.length) {
                break;
            } 
        }
        //printing the answer
        System.out.println("\n- - - North-West corner method - - -");
        printAnswer(feasibleSols, answer);

        //Vogel's approximation method
        matrix = createMatrix(supply, costs, demand);
        feasibleSols = new int[supply.length][demand.length];
        answer = 0;
        int sum;
        int maxApprox;
        int approxX;
        int approxY;
        int minCost1;
        int minCostX;
        int minCostY;
        int minCost2;
        while (true) {
            //check that we can finish
            sum = 0;
            for (int i = 0; i < supply.length; i++) {
                sum += matrix[i][demand.length];
            }
            if (sum == 0) {
                break;
            }
            //find approximations for each row
            for (int i = 0; i < supply.length; i++) {
                minCost1 = matrix[i][0];
                minCost2 = 10000;
                for (int j = 0; j < demand.length; j++) {
                    if (matrix[i][j] < minCost1) {
                        minCost2 = minCost1;
                        minCost1 = matrix[i][j];
                    }
                    if (matrix[i][j] < minCost2 && matrix[i][j] != minCost1) {
                        minCost2 = matrix[i][j];
                    }
                }
                matrix[i][demand.length + 1] = minCost2 - minCost1;
            }
            //find approximations for each column
            for (int i = 0; i < demand.length; i++) {
                minCost1 = matrix[0][i];
                minCost2 = 10000;
                for (int j = 0; j < supply.length; j++) {
                    if (matrix[j][i] < minCost1) {
                        minCost2 = minCost1;
                        minCost1 = matrix[j][i];
                    }
                    if (matrix[j][i] < minCost2 && matrix[j][i] != minCost1) {
                        minCost2 = matrix[j][i];
                    }
                }
                matrix[supply.length + 1][i] = minCost2 - minCost1;
            }
            //find the maximal approximation
            maxApprox = matrix[supply.length + 1][0];
            approxX = supply.length + 1;
            approxY = 0;
            //firstly check the columns
            for (int i = 0; i < demand.length; i++) {
                if (matrix[supply.length + 1][i] > maxApprox) {
                    maxApprox = matrix[supply.length + 1][i];
                    approxX = supply.length + 1;
                    approxY = i;
                }
            }
            //then check the rows
            for (int i = 0; i < supply.length; i++) {
                if (matrix[i][demand.length + 1] > maxApprox) {
                    maxApprox = matrix[i][demand.length + 1];
                    approxX = i;
                    approxY = demand.length + 1;
                }
            }
            //find the min value in the maxApprox column/row
            if (approxX == supply.length + 1) {
                //means that we should find the min value in the column
                minCost1 = matrix[0][approxY];
                minCostX = 0;
                minCostY = approxY;
                for (int i = 0; i < supply.length; i++) {
                    if (matrix[i][approxY] < minCost1) {
                        minCost1 = matrix[i][approxY];
                        minCostX = i;
                        minCostY = approxY;
                    }
                }
            } else {
                //means that we should find the min value in the row
                minCost1 = matrix[approxX][0];
                minCostX = approxX;
                minCostY = 0;
                for (int i = 0; i < demand.length; i++) {
                    if (matrix[approxX][i] < minCost1) {
                        minCost1 = matrix[approxX][i];
                        minCostX = approxX;
                        minCostY = i;
                    }
                }
            }
            //finding the minimal supply/demand
            if (matrix[minCostX][demand.length] < matrix[supply.length][minCostY]) {
                minVariant = matrix[minCostX][demand.length];
                //erase the row
                for (int i = 0; i < demand.length; i++) {
                    matrix[minCostX][i] = 10000;
                }
                //update supplies/demands
                matrix[minCostX][demand.length] -= minVariant;
                matrix[supply.length][minCostY] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[minCostX][minCostY] = minVariant;
                answer += minVariant * costs[minCostX][minCostY];
            } else {
                minVariant = matrix[supply.length][minCostY];
                //erase the column
                for (int i = 0; i < supply.length; i++) {
                    matrix[i][minCostY] = 10000;
                }
                //update supplies/demands
                matrix[minCostX][demand.length] -= minVariant;
                matrix[supply.length][minCostY] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[minCostX][minCostY] = minVariant;
                answer += minVariant * costs[minCostX][minCostY];
            }
        }
        //printing the answer
        System.out.println("- - - Vogel's approximation method - - -");
        printAnswer(feasibleSols, answer);

        //Russell’s approximation method
        matrix = createMatrix(supply, costs, demand);
        feasibleSols = new int[supply.length][demand.length];
        answer = 0;
        int approxMatrix[][] = new int[supply.length][demand.length];
        boolean[][] isNotErasedMatrix = new boolean[supply.length][demand.length];
        int minApprox;
        //create isNotErasedMatrix (for checking that cell is not erased)
        for (int i = 0; i < supply.length; i++) {
            for (int j = 0; j < demand.length; j++) {
                isNotErasedMatrix[i][j] = true;
            }
        }
        while (true) {
            //check that we can finish
            sum = 0;
            for (int i = 0; i < supply.length; i++) {
                sum += matrix[i][demand.length];
            }
            if (sum == 0) {
                break;
            }
            //find max cost for each column
            for (int i = 0; i < demand.length; i++) {
                maxApprox = matrix[0][i];
                for (int j = 0; j < supply.length; j++) {
                    if (matrix[j][i] > maxApprox) {
                        maxApprox = matrix[j][i];
                    }
                }
                matrix[supply.length + 1][i] = maxApprox;
            }
            //find max cost for each row
            for (int i = 0; i < supply.length; i++) {
                maxApprox = matrix[i][0];
                for (int j = 0; j < demand.length; j++) {
                    if (matrix[i][j] > maxApprox) {
                        maxApprox = matrix[i][j];
                    }
                }
                matrix[i][demand.length + 1] = maxApprox;
            }
            //filling the approximation matrix
            for (int i = 0; i < supply.length; i++) {
                for (int j = 0; j < demand.length; j++) {
                    approxMatrix[i][j] = matrix[i][j] - matrix[i][demand.length + 1] - matrix[supply.length + 1][j];
                }
            }
            //finding the minimal approximation
            minApprox = approxMatrix[0][0];
            approxX = 0;
            approxY = 0;
            for (int i = 0; i < supply.length; i++) {
                for (int j = 0; j < demand.length; j++) {
                    if (approxMatrix[i][j] < minApprox && isNotErasedMatrix[i][j]) {
                        minApprox = approxMatrix[i][j];
                        approxX = i;
                        approxY = j;
                    }
                }
            }
            //finding the minimal supply/demand
            if (matrix[approxX][demand.length] < matrix[supply.length][approxY]) {
                minVariant = matrix[approxX][demand.length];
                //erasing the row
                for (int i = 0; i < demand.length; i++) {
                    matrix[approxX][i] = 0;
                    isNotErasedMatrix[approxX][i] = false;
                }
                //update supplies/demands
                matrix[approxX][demand.length] -= minVariant;
                matrix[supply.length][approxY] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[approxX][approxY] = minVariant;
                answer += minVariant * costs[approxX][approxY];
            } else {
                minVariant = matrix[supply.length][approxY];
                //erasing the column
                for (int i = 0; i < supply.length; i++) {
                    matrix[i][approxY] = 0;
                    isNotErasedMatrix[i][approxY] = false;
                }
                //update supplies/demands
                matrix[approxX][demand.length] -= minVariant;
                matrix[supply.length][approxY] -= minVariant;
                //update feasible solutions and answer
                feasibleSols[approxX][approxY] = minVariant;
                answer += minVariant * costs[approxX][approxY];
            }
        }
        //printing the answer
        System.out.println("- - - Russell’s approximation method - - -");
        printAnswer(feasibleSols, answer);
    }

    static int[][] createMatrix(int[] supply, int[][] costs, int[] demand) {
        int[][] matrix = new int[supply.length + 2][demand.length + 2];
        for (int i = 0; i < supply.length; i++) {
            for (int j = 0; j < demand.length; j++) {
                matrix[i][j] = costs[i][j];
            }
            matrix[i][demand.length] = supply[i];
            matrix[i][demand.length + 1] = 0;
        }
        for (int i = 0; i < demand.length; i++) {
            matrix[supply.length][i] = demand[i];
            matrix[supply.length + 1][i] = 0;
        }
        return matrix;
    }

    static void printAnswer(int[][] feasibleSols, int answer) {
        int counter = 1;
        for (int i = 0; i < feasibleSols.length; i++) {
            System.out.print("S" + counter++ + ": [");
            for (int j = 0; j < feasibleSols[i].length - 1; j++) {
                System.out.print(feasibleSols[i][j] + ", ");
            }
            System.out.print(feasibleSols[i][feasibleSols[i].length - 1] + "]\n");
        }
        System.out.println("Answer: " + answer + "\n");
    }
}