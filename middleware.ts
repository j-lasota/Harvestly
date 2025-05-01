import { chain } from '@/middlewares/chain';
import { withI18n } from '@/middlewares/withI18nMiddleware';
import { withAuth } from '@/middlewares/withAuthMiddleware';


export default chain([withI18n, withAuth]);

// const isPublicRoute = [
//    '/',
//    '/about',
//    '/login',
//    '/api/auth/signin'

export const config = {
 matcher: ['/((?!api|_next/static|_next/image|favicon.ico|images).*)'],
};