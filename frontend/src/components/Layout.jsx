import {
  LayoutDashboard,
  Wallet,
  ReceiptText,
  FileText,
  LogIn,
  UserPlus,
  Bell,
  Search,
  Sparkles,
} from "lucide-react";
import { NavLink, Outlet } from "react-router-dom";

function Layout() {
  const navItems = [
    { to: "/", label: "Dashboard", icon: LayoutDashboard },        
    { to: "/transactions", label: "Transactions", icon: ReceiptText },
    { to: "/invoices", label: "Invoices", icon: FileText },
    { to: "/login", label: "Login", icon: LogIn },
    { to: "/register", label: "Register", icon: UserPlus },
  ];

  const linkClass = ({ isActive }) =>
    `group flex items-center gap-3 px-4 py-3 rounded-2xl text-sm font-semibold transition ${
      isActive
        ? "bg-white text-[#07111F] shadow-[0_18px_45px_rgba(34,211,238,0.16)]"
        : "text-slate-300 hover:text-white hover:bg-white/10"
    }`;

  return (
    <div className="min-h-screen flex">
      <aside className="w-72 payflow-gradient text-white p-6 flex flex-col border-r border-white/10 relative overflow-hidden">
        <div className="absolute -top-24 -right-24 h-56 w-56 rounded-full bg-cyan-400/20 blur-3xl" />
        <div className="absolute bottom-32 -left-24 h-56 w-56 rounded-full bg-blue-500/20 blur-3xl" />

        <div className="relative mb-10">
          <div className="h-14 w-14 rounded-3xl bg-white/10 border border-white/15 flex items-center justify-center font-bold text-xl mb-4 payflow-glow">
            <span className="bg-gradient-to-r from-cyan-300 to-blue-400 bg-clip-text text-transparent">
              P
            </span>
          </div>

          <h1 className="text-3xl font-black tracking-tight">PayFlow</h1>
          <p className="text-cyan-100/70 text-sm">Mediterranean Fintech OS</p>
        </div>

        <nav className="relative space-y-2">
          {navItems.map((item) => {
            const Icon = item.icon;

            return (
              <NavLink key={item.to} to={item.to} className={linkClass}>
                <Icon size={18} />
                {item.label}
              </NavLink>
            );
          })}
        </nav>

        <div className="relative mt-auto rounded-[2rem] border border-white/15 bg-white/10 p-5 backdrop-blur-xl overflow-hidden">
          <div className="absolute inset-x-6 top-0 h-px flow-line" />

          <div className="h-10 w-10 rounded-2xl bg-cyan-300/20 text-cyan-200 flex items-center justify-center mb-4">
            <Sparkles size={18} />
          </div>

          <p className="text-sm text-cyan-100/80">PayFlow Signature</p>
          <h3 className="text-lg font-black mt-1">Business Wallet</h3>
          <p className="text-xs text-cyan-100/65 mt-2 leading-relaxed">
            Invoices, payments and wallet intelligence with a premium local
            fintech identity.
          </p>
        </div>
      </aside>

      <div className="flex-1">
        <header className="h-20 bg-white/75 backdrop-blur-xl border-b border-white/60 px-8 flex items-center justify-between">
          <div className="flex items-center gap-3 bg-white/80 border border-slate-200 px-4 py-3 rounded-3xl w-96 shadow-sm">
            <Search size={18} className="text-slate-400" />
            <input
              className="bg-transparent outline-none text-sm w-full placeholder:text-slate-400"
              placeholder="Search flows, invoices, wallets..."
            />
          </div>

          <div className="flex items-center gap-4">
            <button className="h-11 w-11 rounded-2xl bg-white border border-slate-200 shadow-sm flex items-center justify-center hover:bg-slate-50 transition">
              <Bell size={18} />
            </button>

            <div className="flex items-center gap-3 rounded-3xl bg-white border border-slate-200 px-3 py-2 shadow-sm">
              <div className="text-right">
                <p className="text-sm font-bold text-slate-950">
                  Hedi Ben Alaya
                </p>
                <p className="text-xs text-slate-500">Customer</p>
              </div>

              <div className="h-11 w-11 rounded-2xl bg-[#07111F] text-white flex items-center justify-center font-bold">
                H
              </div>
            </div>
          </div>
        </header>

        <main className="p-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default Layout;