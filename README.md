# TPI-AlgoritmosI
Grupo 2. Algoritmos I - Licenciatura en Ciencia de Datos. 

### Clonar el Repositorio
```
git clone https://github.com/usuario/proyecto.git
```

### Crear una Rama Nueva
```
git checkout nombredelarama
```

### Ir a la Rama
```
git branch nombredelarama
```

### Añade las modificaciones a la Rama
```
git add .
```

### Commit con descripción de lo modificado
```
git commit -m "Descripción del cambio realizado"
```

### Subir los cambios a la Rama
```
git push origin nombre-de-la-rama
```

### Crear un Pull Request desde Git Hub. 
```
main <- nombredelarama
```

### Antes de funsionar los cambios a main, verificar que la rama esté actualizada.
```
git checkout main
```
```
git pull origin main
```
```
git checkout nombre-de-la-rama
```
```
git merge main
```

### Una vez aprobado el Pull Request, fusiona tu rama con la rama principal:
```
git checkout main
```
```
git merge nombre-de-la-rama
```
