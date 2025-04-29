import type { NextConfig } from "next";
import createNextIntlPlugin from "next-intl/plugin";

const nextConfig: NextConfig = {};

const withNextintl = createNextIntlPlugin();
export default withNextintl(nextConfig);
