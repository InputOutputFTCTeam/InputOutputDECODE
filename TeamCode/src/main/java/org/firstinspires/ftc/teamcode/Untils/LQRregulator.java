package org.firstinspires.ftc.teamcode.Untils;

public class LQRregulator{


    // ==== Класс LQRController ====
    // Этот контроллер вычисляет мощность на моторы на основе ошибки позиции
    // и скорости (производной ошибки). По сути это PD-контроллер.
    class LQRController {
        double k1, k2;           // коэффициенты LQR: k1 — для позиции, k2 — для скорости (демпфирование)
        double prevError = 0;    // предыдущая ошибка, нужна для вычисления скорости
        long prevTime = System.nanoTime(); // время последнего обновления

        // Конструктор: передаем коэффициенты
        public LQRController(double k1, double k2) {
            this.k1 = k1;
            this.k2 = k2;
        }

        /**
         * update — вычисляет выходной сигнал для моторов
         *
         * @param target  - целевая позиция (энкодеры)
         * @param current - текущая позиция (энкодеры)
         * @return мощность для моторов (-1 .. 1)
         */
        public double update(double target, double current) {
            double error = target - current; // текущая ошибка

            // вычисляем dt (прошедшее время с прошлого обновления) в секундах
            long now = System.nanoTime();
            double dt = (now - prevTime) / 1e9;
            if (dt <= 0) dt = 1e-3; // защита от деления на ноль

            // вычисляем скорость (производная ошибки)
            double velocity = (error - prevError) / dt;

            // сохраняем текущее состояние для следующего шага
            prevError = error;
            prevTime = now;

            // LQR (PD) управление: мощность = k1*позиция + k2*скорость
            return k1 * error + k2 * velocity;
        }

        // сброс предыдущей ошибки (например, перед новым движением)
        public void reset() {
            prevError = 0;
            prevTime = System.nanoTime();
        }
    }

    // ==== Создаем LQR контроллеры для движения и поворота ====
    LQRController driveLQR = new LQRController(0.002, 0.0005); // движение вперёд/назад
    LQRController turnLQR = new LQRController(0.003, 0.0007); // поворот

    // константа для перевода угла в тики энкодера (подбирается экспериментально)
    static final double TICKS_PER_DEGREE = 10;
}
