import { useEffect, useState } from "react";
import api from "../services/api";

function Invoices() {
  const [invoices, setInvoices] = useState([]);

  useEffect(() => {
    api.get("/invoices/me")
      .then((res) => setInvoices(res.data))
      .catch((err) => console.error(err));
  }, []);

  return (
    <div>
      <h1 className="text-3xl font-bold mb-6">Invoices</h1>

      <div className="bg-white rounded-2xl shadow-sm overflow-hidden">
        <table className="w-full">
          <thead className="bg-slate-100">
            <tr>
              <th className="p-4 text-left">Invoice</th>
              <th className="p-4 text-left">Customer</th>
              <th className="p-4 text-left">Amount</th>
              <th className="p-4 text-left">Status</th>
              <th className="p-4 text-left">Created</th>
            </tr>
          </thead>

          <tbody>
            {invoices.map((invoice) => (
              <tr key={invoice.id} className="border-t">
                <td className="p-4">{invoice.invoiceNumber}</td>
                <td className="p-4">{invoice.customerEmail}</td>
                <td className="p-4">
                  {invoice.amount} TND
                </td>
                <td className="p-4">
                  {invoice.status}
                </td>
                <td className="p-4">
                  {new Date(invoice.createdAt).toLocaleDateString()}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default Invoices;