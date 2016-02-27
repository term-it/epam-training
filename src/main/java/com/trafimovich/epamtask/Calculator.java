/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.trafimovich.epamtask;

import java.util.Collections;
import java.util.ListIterator;
import java.util.Stack;

/**
 *
 * @author Term-iT
 */
abstract public class Calculator {

    final String[] OPERATORS = {"+","-","/","*"};
    final String[] FUNCTIONS = {"M","C","M+","M-","sin","exp","sqrt"};

    protected Stack<String> stackOperations = new Stack<>();
    protected Stack<String> stackPOLIZ = new Stack<>();
    protected Stack<String> stackCalc = new Stack<>();
    
    protected String TYPE;
    
    Calculator() {
        stackOperations.clear();
        stackPOLIZ.clear();
        stackCalc.clear();
    }
    
    public void printType() {
        System.out.println(this.TYPE);
    }
    
    public double calculate(ListIterator<String> InputData) {
        printType();
        // Преобразуем входные данные в "обратную польскую запись"
        convertToRPN(InputData);
        // по условиям алгоритма "стековой машины" вычисление проводится
        // слева-направо. Поэтому инвертируем нашу структуру стека
        Collections.reverse(stackPOLIZ);
        // Очищаем стек, в кот-ом будут храниться вычисления
	stackCalc.clear();
        // Пока есть входные данные
        while (!stackPOLIZ.empty()) {
            String token = stackPOLIZ.pop();    // 1. На вход подается очередной токен
            if (isNumber(token)) {              // 2. Если подан "операнд", то -
                stackCalc.push(token);          //    помещаем на вершину стека
            // 3. Если подана "операция", то -
            // соответствующая операция выполняется над требуемым количеством значений,
            // извлечённых из стека, взятых в порядке добавления
            } else if (isOperator(token)) {     // 3.1 т.е. если это бинарная операция, то -
                // извлекаем 2 операнда, преобразуем в числовой эквивалент,
                double operand1 = Double.parseDouble(stackCalc.pop());
                double operand2 = Double.parseDouble(stackCalc.pop());
                // выполняем соответствующую операцию,
                // и результат кладем на вершину стека
                switch (token) {
                    case "+":
                        stackCalc.push(String.valueOf(operand2+operand1));
                        break;
                    case "-":
                        stackCalc.push(String.valueOf(operand2-operand1));
                        break;
                    case "*":
                        stackCalc.push(String.valueOf(operand2*operand1));
                        break;
                    case "/":
                        stackCalc.push(String.valueOf(operand2/operand1));
                        break;
                }
            } else if (isFunction(token)) {     // 3.2 т.е. если это унарная операция, то -
                // извлекаем 1 операнд, преобразуем в числовой эквивалент
                // и результат кладем на вершину стека
                double operand = Double.parseDouble(stackCalc.pop());
                switch (token) {
                    case "sin":
                        stackCalc.push(String.valueOf(Math.sin(Math.toRadians(operand))));
                        break;
                    case "exp":
                        stackCalc.push(String.valueOf(Math.exp(operand)));
                        break;
                    case "sqrt":
                        stackCalc.push(String.valueOf(Math.sqrt(operand)));
                        break;
                }
            }
        }
        return Double.parseDouble(stackCalc.pop());
    }
    
    protected void convertToRPN(ListIterator<String> tokens) {
        // Урезанный алгоритм "сортировочная станция" Э.Дейкстры
        while (tokens.hasNext()) {                  // пока не все токены обработаны
            String token = tokens.next();           //  1. прочитать токен
            if (isNumber(token)) {                  //  2. если токен = число, то -
                stackPOLIZ.push(token);             //     добавить в очередь вывода
            } else if (isFunction(token)) {         //  3. если токен = функция то, -
                stackOperations.push(token);        //     поместить в стек операторов
            } else if (isOperator(token)) {         //  4. если токен = оператор op1 то, -
                while (!stackOperations.empty()     //     (перекладываем пока стек не опустеет и
                        // 4.1 пока на вершине стека присутствует op2
                        && isOperator(stackOperations.lastElement())
                        // и приоритет оператора op1
                        && (definePriority(token)
                        // меньше либо равен op2
                        <= definePriority(stackOperations.lastElement()))) {
                    // то перекладывать op2 из стека в очередь вывода
                    stackPOLIZ.push(stackOperations.pop());
                }
                stackOperations.push(token);        //     4.2 положить op1 в стек операторов
            } else if (isLeftBracket(token)) {      //  5. если токен = открывающая скобка, то -
                stackOperations.push(token);        //     положить его в стек операторов
            } else if (isRightBracket(token)) {     //  6. если токен = закрывающая скобка, то -
                while (!stackOperations.empty()     //     (перекладываем пока стек не опустеет)
                       // 6.1 пока токен на вершине стека не открывающая скобка
                       && !isLeftBracket(stackOperations.lastElement())) {
                       // перекладывать операторы из стека в очередь вывода
                       stackPOLIZ.push(stackOperations.pop());
                }
                // 6.2 выкинуть открывающую скобку из стека, не добавляя в очередь вывода
                stackOperations.pop();
                // 6.3 если токен на вершине стека = функция
                if (isFunction(stackOperations.lastElement())) {
                    // то добавить её в выходную очередь
                    stackPOLIZ.push(stackOperations.pop());
                }
            } else {
                System.out.println("Unexpected item!");
                stackOperations.clear();
                stackPOLIZ.clear();
                return ;
            }
        }
        // 7. когда обработаны все токены (на входе больше не осталось)
        while (!stackOperations.empty()) {          // 7.1 пока есть токены в стеке операторов
            // перекладываем из стека операторов в выходную очередь
            stackPOLIZ.push(stackOperations.pop());
        }
        // Конец
        System.out.println("We've got an expression:\n"+stackPOLIZ);
    }

    protected boolean isNumber (String token) {
        try {
            Double.parseDouble(token);
        }
        catch (Exception exc) {
            return false;
        }
        return true;
    }
    
    protected boolean isOperator(String token) {
	for (String op : OPERATORS)
            if (op.equals(token))
                return true;
        return false;
    }

    protected boolean isLeftBracket(String token) {
        return token.equals("(");
    }
    
    protected boolean isRightBracket(String token) {
        return token.equals(")");
    }
    
    protected boolean isFunction(String token) {
	for (String func : FUNCTIONS) {
            if (func.equalsIgnoreCase(token))
		return true;
	}
	return false;
    }
    
    protected int definePriority(String operation) {
        if (operation.equals("+") || operation.equals("-"))
            return 1;
        else
            return 2;
    }
}
