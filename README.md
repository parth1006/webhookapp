# Bajaj Finserv Health | Qualifier 1 | JAVA

## Task Summary
- On startup, Spring Boot app:
  - Sends a POST to `/generateWebhook/JAVA` with personal details.
  - Parses webhook URL and JWT from response.
  - Sends the `finalQuery` SQL via POST to the given webhook using the JWT.

## Tech Stack
- Java 17
- Spring Boot 3.4.9
- RestTemplate
- H2 (default in-memory DB for setup)

## Final SQL Query
```sql
SELECT p.AMOUNT AS SALARY,
       e.FIRST_NAME || ' ' || e.LAST_NAME AS NAME,
       EXTRACT(YEAR FROM CURRENT_DATE) - EXTRACT(YEAR FROM e.DOB) AS AGE,
       d.DEPARTMENT_NAME
FROM PAYMENTS p
JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID
JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1
ORDER BY p.AMOUNT DESC
LIMIT 1;
