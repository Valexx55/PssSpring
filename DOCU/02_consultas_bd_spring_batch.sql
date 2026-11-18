USE bd_spring_batch;

-- 1. Registros disponibles en origen
SELECT COUNT(*) AS total_origen FROM clientes_origen;
SELECT * FROM clientes_origen ORDER BY id;

-- 2. Registros realmente persistidos
SELECT COUNT(*) AS total_procesados FROM clientes_procesados;
SELECT * FROM clientes_procesados ORDER BY id;

-- 3. Comprobar si ha persistido el chunk del error (15 a 21)
SELECT * FROM clientes_procesados WHERE id BETWEEN 15 AND 21 ORDER BY id;

-- 4. Últimas instancias de Job
SELECT JOB_INSTANCE_ID, JOB_NAME, JOB_KEY, VERSION
FROM BATCH_JOB_INSTANCE ORDER BY JOB_INSTANCE_ID DESC;

-- 5. Ejecuciones, estado y relación con su instancia
SELECT ji.JOB_NAME, ji.JOB_INSTANCE_ID, je.JOB_EXECUTION_ID,
       je.STATUS, je.EXIT_CODE, je.CREATE_TIME,
       je.START_TIME, je.END_TIME
FROM BATCH_JOB_INSTANCE ji
JOIN BATCH_JOB_EXECUTION je ON je.JOB_INSTANCE_ID=ji.JOB_INSTANCE_ID
ORDER BY je.JOB_EXECUTION_ID DESC;

-- 6. Métricas de los Steps
SELECT JOB_EXECUTION_ID, STEP_EXECUTION_ID, STEP_NAME, STATUS,
       READ_COUNT, WRITE_COUNT, COMMIT_COUNT, ROLLBACK_COUNT,
       READ_SKIP_COUNT, PROCESS_SKIP_COUNT, WRITE_SKIP_COUNT
FROM BATCH_STEP_EXECUTION
ORDER BY STEP_EXECUTION_ID DESC;

-- 7. Ejecuciones de una misma instancia (reinicio)
SELECT JOB_INSTANCE_ID, JOB_EXECUTION_ID, STATUS, EXIT_CODE
FROM BATCH_JOB_EXECUTION
WHERE JOB_INSTANCE_ID = 1 -- Sustituir por el ID real
ORDER BY JOB_EXECUTION_ID;

-- 8. Parámetros del Job (inspección; columnas según versión)
SELECT * FROM BATCH_JOB_EXECUTION_PARAMS
ORDER BY JOB_EXECUTION_ID DESC;

-- 9. Contextos persistidos, inspección de estructura
SELECT * FROM BATCH_JOB_EXECUTION_CONTEXT
ORDER BY JOB_EXECUTION_ID DESC;
SELECT * FROM BATCH_STEP_EXECUTION_CONTEXT
ORDER BY STEP_EXECUTION_ID DESC;

-- 10. Distribución por localidad (datos de origen)
SELECT localidad, COUNT(*) AS clientes
FROM clientes_origen GROUP BY localidad
ORDER BY clientes DESC, localidad;

-- 11. Recuento de localidades y localidad con nombre más largo
SELECT COUNT(DISTINCT localidad) AS localidades_distintas
FROM clientes_origen;
SELECT localidad, CHAR_LENGTH(localidad) AS longitud
FROM clientes_origen ORDER BY longitud DESC, localidad LIMIT 1;

-- 12. Limpiar SOLO los datos de salida para repetir una
--     ejecución nueva con parámetros diferentes:
-- DELETE FROM clientes_procesados;
