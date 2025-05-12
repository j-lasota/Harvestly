import createMiddleware from "next-intl/middleware";
import { routing } from "@/i18n/routing";
import { auth } from "@/auth";

// Public routes
const publicRoutes = ["/", "/products", "/store", "/store/[^/]+", "/map"];

export default auth((req) => {
  const publicPathnameRegex = RegExp(
    `^(/(${routing.locales.join("|")}))?(${publicRoutes
      .flatMap((p) => (p === "/" ? ["", "/"] : p))
      .join("|")})/?$`,
    "i"
  );

  const isPublic = publicPathnameRegex.test(req.nextUrl.pathname);

  if (!req.auth && !isPublic) {
    const newUrl = new URL("/signin", req.nextUrl.origin);
    return Response.redirect(newUrl);
  }

  return createMiddleware(routing)(req);
});

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico|manifest.webmanifest|.*\\.(?:svg|png|jpg|jpeg|gif|webp)$).*)",
  ],
};
