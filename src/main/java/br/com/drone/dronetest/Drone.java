package br.com.drone.dronetest;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Drone {
    Point position = new Point();
    Pattern cmd_direction = Pattern.compile("^[NSLO]");
    Pattern step_value = Pattern.compile("[0-9]+");

    /**
     * Percorre os comandos da entrada, alterando a posição do drone até que os
     * comandos se esgotem. Quando a entrada é inválida ou o drone extrapola o
     * espaço permitido (overflow), as coordenadas retornadas são (999, 999).
     * */
    public String changePosition(String str) {
        if (inputIsInvalid(str)) return "(999, 999)";
        str = cleanInput(str);

        Pattern cmd = Pattern.compile("[NSLO][0-9]*");
        Matcher matcher = cmd.matcher(str);

        while (matcher.find()) {
            String next_command = matcher.group(0);
            matcher = cmd.matcher(matcher.replaceFirst(""));

            Point delta_position = readCommand(next_command);

            if (positionWillOverflow(delta_position)) return "(999, 999)";
            else position.translate(delta_position.x, delta_position.y);
        }
        return "(" + position.x + ", " + position.y + ")";
    }

    /**
     * Verifica se a entrada é inválida. Caso algum caractere fora dos permitidos
     * estiverem presentes, eles serão encontrados através da expressão regular.
     * */
    public boolean inputIsInvalid(String input) {
        // Checks if input is invalid
        if (input == null || input == "") return true;
        Pattern pattern = Pattern.compile("[^NSLO0-9X]|X[0-9]+|^[0-9]+");
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    /**
     * Verifica se a transposição do drone para a próxima posição irá ocasionar
     * um Overflow.
     * */
    private boolean positionWillOverflow(Point movement) {
        try {
            Math.addExact(position.x, movement.x);
            Math.subtractExact(position.x, movement.x);
            Math.addExact(position.y, movement.y);
            Math.subtractExact(position.y, movement.y);
            return false;
        } catch (ArithmeticException e) {
            return true;
        }
    }

    /**
     * Limpa a string de entrada, executando os comandos de cancelamento (caractere X).
     * A expressão regular procura por qualquer combinação de direção e X, com ou sem
     * o valor de step e remove esses caracteres da string, retornando apenas os comandos
     * de movimento.
     * */
    private String cleanInput(String input) {
        Pattern pattern = Pattern.compile("[NSLO][0-9]*X");
        while (pattern.matcher(input).find()) {
            input = pattern.matcher(input).replaceAll("");
        }
        return input;
    }

    /**
     * Lê o próximo comando a ser executado e retorna o delta, ou seja, a diferença a ser
     * aplicada sobre a posição atual do drone.
     * */
    private Point readCommand(String command) {
        Point delta_position = new Point();
        Matcher direction_matcher = cmd_direction.matcher(command);
        Matcher step_matcher = step_value.matcher(command);
        int step = 1;

        // Acha o comando de direção e o separa no primeiro grupo
        direction_matcher.find();
        String direction = direction_matcher.group(0);

        // Verifica se o comando é acompanhado de step
        if (step_matcher.find()) {
            step = Integer.parseInt(step_matcher.group(0));
        }

        if (direction.equals("N")) delta_position.y = step;
        if (direction.equals("S")) delta_position.y = step * -1;
        if (direction.equals("L")) delta_position.x = step;
        if (direction.equals("O")) delta_position.x = step * -1;

        return delta_position;
    }
}
