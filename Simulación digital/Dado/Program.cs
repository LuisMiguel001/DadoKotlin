using System;
using System.Data;

class Program
{
    static void Main(string[] args)
    {
        double[] probabilidades = new double[6] { 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6, 1.0 / 6 };
        Random random = new Random();
        bool continuar = true;

        while (continuar)
        {
            Console.WriteLine("Probabilidades actuales:");
            for (int i = 0; i < 6; i++)
            {
                Console.WriteLine($"Número {i + 1}: {probabilidades[i]:P2}");
            }

            Console.WriteLine("\n¿Desea cambiar las probabilidades de algún número? (s/n)");
            string cambiarProbabilidades = Console.ReadLine();

            if (cambiarProbabilidades.ToLower() == "s")
            {
                Console.WriteLine("¿A cuál número del dado (1-6) le quieres asignar el porcentaje?");
                int numeroLado = int.Parse(Console.ReadLine());

                if (numeroLado < 1 || numeroLado > 6)
                {
                    Console.WriteLine("\tNúmero inválido. Por favor, ingrese un número entre 1 y 6.");
                    continue;
                }

                Console.WriteLine("¿Qué porcentaje le quieres asignar a este número? (en porcentaje, por ejemplo, 20 para 20%)");
                double porcentaje = double.Parse(Console.ReadLine()) / 100.0;

                if (porcentaje < 0 || porcentaje > 1)
                {
                    Console.WriteLine("\tPorcentaje inválido. Debe estar entre 0% y 100%.");
                    continue;
                }

                double restante = 1 - porcentaje;
                for (int i = 0; i < 6; i++)
                {
                    if (i == numeroLado - 1)
                    {
                        probabilidades[i] = porcentaje;
                    }
                    else
                    {
                        probabilidades[i] = restante / 5;
                    }
                }
            }

            Console.WriteLine("\nDesea lanzar el dado? (s/n)");
            string lanzarDado = Console.ReadLine();
            bool seguirLanzando = true;
            while (seguirLanzando)
            {

                if (lanzarDado.ToLower() == "s")
                {
                    double valor = random.NextDouble();
                    double acumulado = 0.0;
                    for (int i = 0; i < 6; i++)
                    {
                        acumulado += probabilidades[i];
                        if (valor < acumulado)
                        {
                            Console.WriteLine($"El dado cayó en: {i + 1}");
                            break;
                        }
                    }
                }
                else
                {
                    seguirLanzando = false;
                }

                Console.WriteLine("\n¿Desea continuar lanzando? (s/n)");
                string seguirLanzandoSinCambiar = Console.ReadLine();
                if (seguirLanzandoSinCambiar.ToLower() != "s")
                {
                    seguirLanzando = false;
                }
            }

            Console.WriteLine("\n¿Desea continuar? (s/n)");
            string seguir = Console.ReadLine();
            if (seguir.ToLower() != "s")
            {
                continuar = false;
            }
        }
        Console.ReadKey();
    }
}
