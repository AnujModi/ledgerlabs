# LedgerLabs

LedgerLabs is an interview-focused digital banking platform built as a production-minded modular monolith. It pairs a Java 21 and Spring Boot API with a React 19 and TypeScript client, using automated tests and independent CI workflows for both applications.

**Live dashboard:** [anujmodi.github.io/ledgerlabs](https://anujmodi.github.io/ledgerlabs/)

## Current experience

- Responsive banking dashboard with account balance, cash-flow metrics, and transaction activity
- Live React integration with the Spring Boot account and transaction APIs
- Client-side transaction filtering with accessible controls
- Focused controller, service, and component tests developed with red-green-refactor TDD
- GitHub Actions verification for Java and React changes

## Run locally

Start the API:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

In a second terminal, start the client:

```powershell
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Vite proxies `/api` requests to Spring Boot on port `8080`.

The published GitHub Pages site uses representative demo data so it remains usable without a public API. Set `VITE_API_BASE_URL` during a production build to connect it to a deployed Spring Boot service.

## Verify

```powershell
cd backend
.\mvnw.cmd verify

cd ..\frontend
npm test
npm run lint
npm run build
```

## Roadmap

PostgreSQL persistence, authenticated customer sessions, transfers with idempotency and optimistic locking, Docker Compose, observability, and cloud deployment.
