# analyze_results.py
import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from pathlib import Path

class MatrixAnalyzer:
    def __init__(self):
        self.results_dir = Path('../results')
        self.plots_dir = Path('./plots')
        self.plots_dir.mkdir(exist_ok=True)
        plt.style.use('default')

    def load_data(self):
        try:
            df = pd.read_csv(self.results_dir / 'times.txt')
            # Filtrar líneas de comentarios y valores negativos
            df = df[~df['implementation'].str.startswith('#')]
            df = df[df['time'] > 0]
            # Convertir threads a int
            df['threads'] = df['threads'].astype(int)
            
            print("Data shape:", df.shape)
            print("Implementations:", df['implementation'].unique())
            print("Thread counts:", sorted(df['threads'].unique()))
            return df
        except Exception as e:
            print("Error loading data:", e)
            return None

    def plot_execution_times(self, df):
        plt.figure(figsize=(12, 6))
        
        # Agrupar y calcular medias
        means = df.groupby(['implementation', 'threads'])['time'].mean().reset_index()
        print("\nMean times:")
        print(means)
        
        # Ordenar los thread counts correctamente
        thread_counts = sorted(means[means['implementation'] != 'sequential']['threads'].unique())
        
        # Preparar datos para el gráfico
        bar_width = 0.35
        r1 = np.arange(len(thread_counts))
        r2 = [x + bar_width for x in r1]
        
        # Preparar datos para cada implementación
        exec_times = []
        fj_times = []
        
        for threads in thread_counts:
            exec_data = means[(means['implementation'] == 'executor') & (means['threads'] == threads)]
            fj_data = means[(means['implementation'] == 'forkjoin') & (means['threads'] == threads)]
            
            exec_times.append(exec_data['time'].values[0] if not exec_data.empty else 0)
            fj_times.append(fj_data['time'].values[0] if not fj_data.empty else 0)
        
        # Graficar barras
        plt.bar(r1, exec_times, width=bar_width, label='Executor', color='skyblue')
        plt.bar(r2, fj_times, width=bar_width, label='ForkJoin', color='lightgreen')
        
        # Línea secuencial
        seq_time = means[means['implementation'] == 'sequential']['time'].iloc[0]
        plt.axhline(y=seq_time, color='r', linestyle='--', 
                   label=f'Sequential ({seq_time:.2f}s)')
        
        # Configurar gráfico
        plt.xlabel('Number of Threads')
        plt.ylabel('Time (seconds)')
        plt.title('Matrix Multiplication Execution Time')
        plt.xticks([r + bar_width/2 for r in r1], thread_counts)
        plt.legend()
        plt.grid(True, alpha=0.3)
        
        plt.savefig(self.plots_dir / 'execution_times.png')
        plt.close()

    def plot_speedup(self, df):
        plt.figure(figsize=(12, 6))
        
        # Calcular speedup
        seq_time = df[df['implementation'] == 'sequential']['time'].mean()
        means = df.groupby(['implementation', 'threads'])['time'].mean().reset_index()
        means = means[means['implementation'] != 'sequential']
        means['speedup'] = seq_time / means['time']
        
        # Graficar por implementación
        for impl in ['executor', 'forkjoin']:
            data = means[means['implementation'] == impl]
            data = data.sort_values('threads')  # Ordenar por threads
            plt.plot(data['threads'], data['speedup'], 'o-', 
                    label=impl.capitalize(), linewidth=2, markersize=8)
        
        # Línea ideal
        max_threads = means['threads'].max()
        plt.plot([1, max_threads], [1, max_threads], 'k--', label='Ideal', linewidth=1)
        
        plt.xlabel('Number of Threads')
        plt.ylabel('Speedup')
        plt.title('Speedup vs Number of Threads')
        plt.grid(True)
        plt.legend()
        
        plt.savefig(self.plots_dir / 'speedup.png')
        plt.close()

    def plot_efficiency(self, df):
        plt.figure(figsize=(12, 6))
        
        # Calcular efficiency
        seq_time = df[df['implementation'] == 'sequential']['time'].mean()
        means = df.groupby(['implementation', 'threads'])['time'].mean().reset_index()
        means = means[means['implementation'] != 'sequential']
        means['efficiency'] = (seq_time / means['time']) / means['threads']
        
        # Graficar por implementación
        for impl in ['executor', 'forkjoin']:
            data = means[means['implementation'] == impl]
            data = data.sort_values('threads')  # Ordenar por threads
            plt.plot(data['threads'], data['efficiency'], 'o-', 
                    label=impl.capitalize(), linewidth=2, markersize=8)
        
        plt.axhline(y=1, color='k', linestyle='--', label='Ideal', linewidth=1)
        
        plt.xlabel('Number of Threads')
        plt.ylabel('Efficiency')
        plt.title('Efficiency vs Number of Threads')
        plt.grid(True)
        plt.legend()
        
        plt.savefig(self.plots_dir / 'efficiency.png')
        plt.close()

    def generate_report(self, df):
        report_path = self.plots_dir / 'analysis_report.txt'
        
        with open(report_path, 'w') as f:
            f.write("MATRIX MULTIPLICATION PERFORMANCE ANALYSIS\n")
            f.write("=======================================\n\n")
            
            # Sequential baseline
            seq_time = df[df['implementation'] == 'sequential']['time'].mean()
            seq_std = df[df['implementation'] == 'sequential']['time'].std()
            f.write(f"Sequential Implementation\n")
            f.write(f"------------------------\n")
            f.write(f"Average time: {seq_time:.3f} ± {seq_std:.3f} seconds\n\n")
            
            # Parallel implementations
            for impl in ['executor', 'forkjoin']:
                impl_data = df[df['implementation'] == impl]
                f.write(f"\n{impl.upper()} Implementation\n")
                f.write("-" * (len(impl) + 16) + "\n")
                
                for threads in sorted(impl_data['threads'].unique()):
                    thread_data = impl_data[impl_data['threads'] == threads]
                    mean_time = thread_data['time'].mean()
                    std_time = thread_data['time'].std()
                    speedup = seq_time / mean_time
                    efficiency = speedup / threads
                    
                    f.write(f"\nThreads: {threads}\n")
                    f.write(f"  Time: {mean_time:.3f} ± {std_time:.3f} seconds\n")
                    f.write(f"  Speedup: {speedup:.2f}x\n")
                    f.write(f"  Efficiency: {efficiency:.2f}\n")

def main():
    analyzer = MatrixAnalyzer()
    
    print("Loading data...")
    df = analyzer.load_data()
    
    if df is not None:
        print("\nGenerating plots...")
        analyzer.plot_execution_times(df)
        analyzer.plot_speedup(df)
        analyzer.plot_efficiency(df)
        
        print("Generating report...")
        analyzer.generate_report(df)
        
        print("\nAnalysis completed. Results saved in 'plots' directory.")
    else:
        print("Error: Could not load data. Please check your results file.")

if __name__ == "__main__":
    main()